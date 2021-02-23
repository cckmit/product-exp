package com.tmb.oneapp.productsexpservice.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.activitylog.ActivityLogs;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.portdata.Port;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRq;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.*;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsData;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListPage;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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
    private final KafkaProducerService kafkaProducerService;
    private final String investmentStartTime;
    private final String investmentEndTime;
    private final String topicName;

    @Autowired
    public ProductsExpService(InvestmentRequestClient investmentRequestClient,
                              AccountRequestClient accountRequestClient,
                              KafkaProducerService kafkaProducerService,
                              @Value("${investment.close.time.start}") String investmentStartTime,
                              @Value("${investment.close.time.end}") String investmentEndTime,
                              @Value("${com.tmb.oneapp.service.activity.topic.name}") final String topicName) {

        this.investmentRequestClient = investmentRequestClient;
        this.kafkaProducerService = kafkaProducerService;
        this.accountRequestClient = accountRequestClient;
        this.investmentStartTime = investmentStartTime;
        this.investmentEndTime = investmentEndTime;
        this.topicName = topicName;
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


    /**
     * Generic Method to call MF Service getFundAccDetail
     *
     * @param ffsRequestBody
     * @param correlationId
     * @return FfsRsAndValidation
     */
    @LogAround
    public FfsRsAndValidation getFundFFSAndValidation(String correlationId, FfsRequestBody ffsRequestBody) {
        FfsRsAndValidation ffsRsAndValidation = new FfsRsAndValidation();
        final boolean isNotValid = true;
        boolean isStoped = false;
        if(UtilMap.isBusinessClose(investmentStartTime, investmentEndTime, true)){
            ffsRsAndValidation.setServiceClose(isNotValid);
            ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.SERVICE_OUR_CLOSE);
            ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.SERVICE_OUR_CLOSE_MESSAGE);
            ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.SERVICE_OUR_CLOSE_DESC);
            isStoped = true;
        }
        if(!isStoped && !StringUtils.isEmpty(ffsRequestBody.getProcessFlag()) && isServiceClose(correlationId, ffsRequestBody)){
            ffsRsAndValidation.setFundOfShelf(isNotValid);
            ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.OF_SHELF_FUND_CODE);
            ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.OF_SHELF_FUND_MESSAGE);
            ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.OF_SHELF_FUND_DESC);
            isStoped = true;
        }
        if(!isStoped && isBusinessClose(correlationId, ffsRequestBody)){
            ffsRsAndValidation.setNotBusinessOur(isNotValid);
            ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.BUSINESS_HOURS_CLOSE_CODE);
            ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.BUSINESS_HOURS_CLOSE_MESSAGE);
            ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.BUSINESS_HOURS_CLOSE_DESC);
            isStoped = true;
        }
        if(!isStoped && isCASADormant(correlationId, ffsRequestBody)){
            ffsRsAndValidation.setNotBusinessOur(isNotValid);
            ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.CASA_DORMANT_ACCOUNT_CODE);
            ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.CASA_DORMANT_ACCOUNT_MESSAGE);
            ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.CASA_DORMANT_ACCOUNT_DESC);
            isStoped = true;
        }
        if(!isStoped){
            ResponseEntity<TmbOneServiceResponse<FfsResponse>> responseEntity = null;
            try {
                Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
                responseEntity = investmentRequestClient.callInvestmentFundFactSheetService(invHeaderReqParameter, ffsRequestBody);
                logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseEntity);
                if (!StringUtils.isEmpty(responseEntity) && responseEntity.getStatusCode() == HttpStatus.OK) {
                    FfsData ffsData = new FfsData();
                    ffsData.setFactSheetData(responseEntity.getBody().getData().getBody().getFactSheetData());
                    ffsRsAndValidation.setBody(ffsData);
                } else {
                    ffsRsAndValidation.setError(true);
                    ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE);
                    ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE);
                    ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE);
                }
            }catch (Exception e){
                logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            }
        }
        return ffsRsAndValidation;
    }

    /**
     * Method isServiceClose
     *
     * @param correlationId
     * @param ffsRequestBody
     */
    public boolean isServiceClose(String correlationId, FfsRequestBody ffsRequestBody){
        ResponseEntity<TmbOneServiceResponse<FundListPage>> responseResponseEntity = null;
        try{
            Map<String, Object> invHeaderReqParameter = UtilMap.createHeader(correlationId, 139, 0);
            responseResponseEntity = investmentRequestClient.callInvestmentFundListInfoService(invHeaderReqParameter);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseResponseEntity);
            if (!StringUtils.isEmpty(responseResponseEntity) &&
                    HttpStatus.OK == responseResponseEntity.getStatusCode()) {
                return UtilMap.isOfShelfCheck(ffsRequestBody, responseResponseEntity.getBody().getData().getFundClassList());
            }
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return true;
        }
        return true;
    }

    /**
     * Method isBusinessClose for check service our
     *
     * @param correlationId
     * @param ffsRequestBody
     */
    public boolean isBusinessClose(String correlationId, FfsRequestBody ffsRequestBody){
        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(ffsRequestBody.getFundCode());
        fundRuleRequestBody.setFundHouseCode(ffsRequestBody.getFundHouseCode());
        fundRuleRequestBody.setTranType(ProductsExpServiceConstant.FUND_RULE_TRANS_TYPE);

        ResponseEntity<TmbOneServiceResponse<FundRuleBody>> responseEntity = null;
        try{
            Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
            responseEntity = investmentRequestClient.callInvestmentFundRuleService(invHeaderReqParameter, fundRuleRequestBody);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseEntity);
            if(!StringUtils.isEmpty(responseEntity) &&
                    HttpStatus.OK == responseEntity.getStatusCode()){
                FundRuleBody fundRuleBody = responseEntity.getBody().getData();
                FundRuleInfoList fundRuleInfoList = fundRuleBody.getFundRuleInfoList().get(0);
                return UtilMap.isBusinessClose(fundRuleInfoList.getTimeStart(), fundRuleInfoList.getTimeEnd(), false);
            }
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return true;
        }
        return true;
    }


    /**
     * Method isCASADormant
     *
     * @param correlationId
     * @param ffsRequestBody
     */
    public boolean isCASADormant(String correlationId, FfsRequestBody ffsRequestBody){
        String responseCustomerExp = null;
        try{
            Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
            responseCustomerExp = accountRequestClient.callCustomerExpService(invHeaderReqParameter, ffsRequestBody.getCrmId());
            logger.info(ProductsExpServiceConstant.CUSTOMER_EXP_SERVICE_RESPONSE, responseCustomerExp);
            return UtilMap.isCASADormant(responseCustomerExp);
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return true;
        }
    }


    /**
     * Method constructActivityLogDataForBuyHoldingFund
     *
     * @param correlationId
     * @param status
     * @param failReason
     * @param crmId
     * @param activityType
     * @param processFlag
     */
    public ActivityLogs constructActivityLogDataForBuyHoldingFund(String correlationId, String status,
                                                                  String failReason,
                                                                  String crmId,
                                                                  String activityType,
                                                                  String processFlag){
        ActivityLogs activityData = new ActivityLogs(correlationId, String.valueOf(System.currentTimeMillis()),
                ProductsExpServiceConstant.ACTIVITY_ID_INVESTMENT_STATUS_TRACKING);
        activityData.setActivityStatus(status);
        activityData.setChannel(ProductsExpServiceConstant.ACTIVITY_LOG_CHANNEL);
        activityData.setAppVersion(ProductsExpServiceConstant.ACTIVITY_LOG_APP_VERSION);
        activityData.setFailReason(failReason);
        activityData.setActivityType(activityType);
        activityData.setCrmId(crmId);
        activityData.setVerifyFlag(processFlag);
        activityData.setReason(failReason);
        return activityData;
    }


    /**
     * Method logactivity
     *
     * @param data
     */
    @Async
    @LogAround
    public void logactivity(ActivityLogs data) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            String output = mapper.writeValueAsString(data);
            logger.info("Activity Data request is  {} : ", output);
            logger.info("Activity Data request topicName is  {} : ", topicName);
            kafkaProducerService.sendMessageAsync(topicName, output);
            logger.info("callPostEventService -  data posted to activity_service : {}", System.currentTimeMillis());
        } catch (Exception e) {
            logger.info("Unable to log the activity request : {}", e.toString());
        }
    }



}
