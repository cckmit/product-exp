package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.portdata.Port;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRq;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.*;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.util.CacheService;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * ProductsExpService class will get fund Details from MF Service
 */
@Service
public class ProductsExpService {
    private static TMBLogger<ProductsExpService> logger = new TMBLogger<>(ProductsExpService.class);
    private InvestmentRequestClient investmentRequestClient;
    private AccountRequestClient accountRequestClient;
    private final CacheService cacheService;

    @Autowired
    public ProductsExpService(InvestmentRequestClient investmentRequestClient, AccountRequestClient accountRequestClient, CacheService cacheService) {
        this.investmentRequestClient = investmentRequestClient;
        this.accountRequestClient = accountRequestClient;
        this.cacheService = cacheService;
    }


    /**
     * Generic Method to call MF Service getFundAccDetail
     *
     * @param fundAccountRq
     * @param correlationId
     * @return
     */
    @LogAround
    public FundAccountRs getFundAccountDetail(String correlationId, FundAccountRq fundAccountRq) {
        FundAccountRs fundAccountRs = null;
        FundAccountRequestBody fundAccountRequestBody = new FundAccountRequestBody();
        fundAccountRequestBody.setFundCode(fundAccountRq.getFundCode());
        fundAccountRequestBody.setServiceType(fundAccountRq.getServiceType());
        fundAccountRequestBody.setUnitHolderNo(fundAccountRq.getUnitHolderNo());

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(fundAccountRq.getFundCode());
        fundRuleRequestBody.setFundHouseCode(fundAccountRq.getFundHouseCode());
        fundRuleRequestBody.setTranType(fundAccountRq.getTranType());

        Map<String, String> invHeaderReqParameter = createHeader(correlationId);
        ResponseEntity<TmbOneServiceResponse<AccDetailBody>> response = null;
        ResponseEntity<TmbOneServiceResponse<FundRuleBody>> responseEntity = null;
        try {
            response = investmentRequestClient.callInvestmentFundAccDetailService(invHeaderReqParameter, fundAccountRequestBody);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, response);
            responseEntity = investmentRequestClient.callInvestmentFundRuleService(invHeaderReqParameter, fundRuleRequestBody);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseEntity);
            UtilMap map = new UtilMap();
            fundAccountRs = map.validateTMBResponse(response, responseEntity);
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return fundAccountRs;
        }
        return fundAccountRs;
    }


    /**
     * Generic Method to create HTTP Header
     *
     * @param correlationId
     * @return
     */
    private Map<String, String> createHeader(String correlationId) {
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return invHeaderReqParameter;
    }


    /**
     * Get fund summary fund summary response.
     *
     * @param correlationId the correlation id
     * @param rq            the rq
     * @return the fund summary response
     */
    public FundSummaryBody getFundSummary(String correlationId, FundSummaryRq rq) {
        FundSummaryBody result = new FundSummaryBody();


        String portData;
        ResponseEntity<TmbOneServiceResponse<com.tmb.oneapp.productsexpservice.model
                .fundsummarydata.response.fundsummary.FundSummaryResponse>> fundSummaryData = null;
        UnitHolder unitHolder = new UnitHolder();

        Map<String, String> invHeaderReqParameter = createHeader(correlationId);
        try {
            portData = accountRequestClient.getPortList(invHeaderReqParameter, rq.getCrmId());
            if (!StringUtils.isEmpty(portData)) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode node = mapper.readValue(portData, JsonNode.class);
                JsonNode dataNode = node.get("data");
                JsonNode portList = dataNode.get("mutual_fund_accounts");
                List<Port> ports = mapper.readValue(portList.toString(), new TypeReference<List<Port>>() {
                });


                String acctNbrList = ports.stream().map(Port::<String>getAcctNbr).collect(Collectors.joining(","));
                unitHolder.setUnitHolderNo(acctNbrList);
                fundSummaryData = investmentRequestClient.callInvestmentFundSummaryService(invHeaderReqParameter
                        , unitHolder);
                if (HttpStatus.OK.value() == fundSummaryData.getStatusCode().value()) {
                    var body = fundSummaryData.getBody();
                    if (body != null) {
                        result.setFundClassList(body.getData().getBody().getFundClassList());
                        result.setFeeAsOfDate(body.getData().getBody().getFeeAsOfDate());
                        result.setPercentOfFundType(body.getData().getBody().getPercentOfFundType());
                        result.setSumAccruedFee(body.getData().getBody().getSumAccruedFee());
                        String jsonInString = objectMapper.writeValueAsString(result);
                        cacheService.set(ProductsExpServiceConstant.REDIS_KEY_FUND_SUMMARY, jsonInString);
                        cacheService.set(ProductsExpServiceConstant.REDIS_KEY_PORTLIST, acctNbrList);
                    }
                }

            }
            return result;
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return null;

        }
    }

    /**
     * Generic Method to call MF Service getFundAccDetail
     *
     * @param fundPaymentDetailRq
     * @param correlationId
     * @return
     */
    @LogAround
    public FundPaymentDetailRs getFundPrePaymentDetail(String correlationId, FundPaymentDetailRq fundPaymentDetailRq) {

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(fundPaymentDetailRq.getFundCode());
        fundRuleRequestBody.setFundHouseCode(fundPaymentDetailRq.getFundHouseCode());
        fundRuleRequestBody.setTranType(fundPaymentDetailRq.getTranType());

        Map<String, String> invHeaderReqParameter = createHeader(correlationId);
        ResponseEntity<TmbOneServiceResponse<FundRuleBody>> responseEntity = null;
        ResponseEntity<TmbOneServiceResponse<FundHolidayBody>> responseFundHoliday = null;
        String responseCustomerExp = null;
        FundPaymentDetailRs fundPaymentDetailRs = null;
        try {

            responseEntity = investmentRequestClient.callInvestmentFundRuleService(invHeaderReqParameter, fundRuleRequestBody);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseEntity);
            responseFundHoliday = investmentRequestClient.callInvestmentFundHolidayService(invHeaderReqParameter, fundPaymentDetailRq.getFundCode());
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseFundHoliday);
            responseCustomerExp = accountRequestClient.callCustomerExpService(invHeaderReqParameter, fundPaymentDetailRq.getCrmId());
            logger.info(ProductsExpServiceConstant.CUSTOMER_EXP_SERVICE_RESPONSE, responseCustomerExp);
            UtilMap map = new UtilMap();
            fundPaymentDetailRs = map.mappingPaymentResponse(responseEntity, responseFundHoliday, responseCustomerExp);
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return fundPaymentDetailRs;
        }
        return fundPaymentDetailRs;
    }


}
