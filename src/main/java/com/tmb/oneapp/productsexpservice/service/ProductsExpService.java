package com.tmb.oneapp.productsexpservice.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.CommonTime;
import com.tmb.common.model.CustomerProfileResponseData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.activitylog.ActivityLogs;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.*;
import com.tmb.oneapp.productsexpservice.model.portdata.Port;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.alternative.AlternativeRq;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRq;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRq;
import com.tmb.oneapp.productsexpservice.model.request.suitability.SuitabilityBody;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.*;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsData;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListPage;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
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
    private CustomerServiceClient customerServiceClient;
    private CommonServiceClient commonServiceClient;
    private final KafkaProducerService kafkaProducerService;
    private final String topicName;

    @Autowired
    public ProductsExpService(InvestmentRequestClient investmentRequestClient,
                              AccountRequestClient accountRequestClient,
                              KafkaProducerService kafkaProducerService,
                              CustomerServiceClient customerServiceClient,
                              CommonServiceClient commonServiceClient,
                              @Value("${com.tmb.oneapp.service.activity.topic.name}") final String topicName) {

        this.investmentRequestClient = investmentRequestClient;
        this.customerServiceClient = customerServiceClient;
        this.kafkaProducerService = kafkaProducerService;
        this.accountRequestClient = accountRequestClient;
        this.commonServiceClient = commonServiceClient;
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

        OrderStmtByPortRq orderStmtByPortRq = new OrderStmtByPortRq();
        orderStmtByPortRq.setPortfolioNumber(fundAccountRq.getUnitHolderNo());
        orderStmtByPortRq.setFundCode(fundAccountRq.getFundCode());
        orderStmtByPortRq.setRowStart(ProductsExpServiceConstant.FIXED_START_PAGE);
        orderStmtByPortRq.setRowEnd(ProductsExpServiceConstant.FIXED_END_PAGE);

        Map<String, String> invHeaderReqParameter = createHeader(correlationId);
        ResponseEntity<TmbOneServiceResponse<AccDetailBody>> response = null;
        ResponseEntity<TmbOneServiceResponse<FundRuleBody>> responseEntity = null;
        ResponseEntity<TmbOneServiceResponse<StatementResponse>> responseStmt = null;
        try {
            response = investmentRequestClient.callInvestmentFundAccDetailService(invHeaderReqParameter, fundAccountRequestBody);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, response);
            responseEntity = investmentRequestClient.callInvestmentFundRuleService(invHeaderReqParameter, fundRuleRequestBody);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseEntity);
            responseStmt = investmentRequestClient.callInvestmentStmtByPortService(invHeaderReqParameter, orderStmtByPortRq);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseEntity);
            UtilMap map = new UtilMap();
            fundAccountRs = map.validateTMBResponse(response, responseEntity, responseStmt);
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
    @LogAround
    public FundSummaryBody getFundSummary(String correlationId, FundSummaryRq rq) {
        FundSummaryBody result = new FundSummaryBody();


        String portData;
        ResponseEntity<TmbOneServiceResponse<FundSummaryResponse>> fundSummaryData = null;
        UnitHolder unitHolder = new UnitHolder();

        Map<String, String> invHeaderReqParameter = createHeader(correlationId);
        try {
            portData = accountRequestClient.getPortList(invHeaderReqParameter, rq.getCrmId());
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, portData);
            if (!StringUtils.isEmpty(portData)) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readValue(portData, JsonNode.class);
                JsonNode dataNode = node.get("data");
                JsonNode portList = dataNode.get("mutual_fund_accounts");
                List<Port> ports = mapper.readValue(portList.toString(), new TypeReference<List<Port>>() {
                });
                List<String> myPorts  = new ArrayList<>();
                for(Port port : ports){
                    myPorts.add(port.getAcctNbr());
                }
                result.setPortsUnitHolder(myPorts);
                String acctNbrList = ports.stream().map(Port::<String>getAcctNbr).collect(Collectors.joining(","));
                unitHolder.setUnitHolderNo(acctNbrList);
                fundSummaryData = investmentRequestClient.callInvestmentFundSummaryService(invHeaderReqParameter, unitHolder);
                logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, fundSummaryData);
                if (HttpStatus.OK.value() == fundSummaryData.getStatusCode().value()) {
                    var body = fundSummaryData.getBody();
                    if (body != null) {
                        FundClassList fundClassList = body.getData().getBody().getFundClassList();
                        List<FundClass> fundClass = fundClassList.getFundClass();
                        List<FundClass> fundClassData = UtilMap.mappingFundListData(fundClass);
                        List<FundSearch> searchList = UtilMap.mappingFundSearchListData(fundClass);

                        result.setFundClass(fundClassData);
                        result.setSearchList(searchList);
                        result.setFundClassList(null);
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
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> responseCommon = null;
        String responseCustomerExp = null;
        FundPaymentDetailRs fundPaymentDetailRs = null;
        try {
            responseEntity = investmentRequestClient.callInvestmentFundRuleService(invHeaderReqParameter, fundRuleRequestBody);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseEntity);
            responseFundHoliday = investmentRequestClient.callInvestmentFundHolidayService(invHeaderReqParameter, fundPaymentDetailRq.getFundCode());
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseFundHoliday);
            responseCustomerExp = accountRequestClient.callCustomerExpService(invHeaderReqParameter, fundPaymentDetailRq.getCrmId());
            logger.info(ProductsExpServiceConstant.CUSTOMER_EXP_SERVICE_RESPONSE, responseCustomerExp);
            responseCommon = commonServiceClient.getCommonConfigByModule(correlationId, ProductsExpServiceConstant.INVESTMENT_MODULE_VALUE);
            logger.info(ProductsExpServiceConstant.CUSTOMER_EXP_SERVICE_RESPONSE, responseCommon);
            UtilMap map = new UtilMap();
            fundPaymentDetailRs = map.mappingPaymentResponse(responseEntity, responseFundHoliday, responseCommon, responseCustomerExp);
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return fundPaymentDetailRs;
        }
        return fundPaymentDetailRs;
    }


    /**
     * Generic Method to call MF Service getFundFFSAndValidation
     *
     * @param ffsRequestBody
     * @param correlationId
     * @return FfsRsAndValidation
     */
    @LogAround
    public FfsRsAndValidation getFundFFSAndValidation(String correlationId, FfsRequestBody ffsRequestBody) {
        FfsRsAndValidation ffsRsAndValidation = new FfsRsAndValidation();
        FundResponse fundResponse = null;
        fundResponse = isServiceHour(correlationId, fundResponse);
        if(StringUtils.isEmpty(fundResponse)) {
            ffsRsAndValidation = validationAlternativeFlow(correlationId, ffsRequestBody, ffsRsAndValidation);
            if (!ffsRsAndValidation.isError()) {
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
                } catch (Exception e) {
                    logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
                    return null;
                }
            }
        }else{
            ffsRsAndValidation.setError(true);
            ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.SERVICE_OUR_CLOSE);
            ffsRsAndValidation.setErrorMsg(fundResponse.getErrorMsg());
            ffsRsAndValidation.setErrorDesc(fundResponse.getErrorDesc());
        }
        return ffsRsAndValidation;
    }

    /**
     * Generic Method to validate AlternativeSellAndSwitch
     *
     * @param alternativeRq
     * @param correlationId
     * @return FundResponse
     */
    @LogAround
    public FundResponse validateAlternativeSellAndSwitch(String correlationId, AlternativeRq alternativeRq) {
        FundResponse fundResponse = new FundResponse();
        fundResponse = isServiceHour(correlationId, fundResponse);
         if(!fundResponse.isError()){
            FfsRequestBody ffsRequestBody = new FfsRequestBody();
            ffsRequestBody.setUnitHolderNo(alternativeRq.getUnitHolderNo());
            ffsRequestBody.setProcessFlag(alternativeRq.getProcessFlag());
            ffsRequestBody.setCrmId(alternativeRq.getCrmId());
            fundResponse = validationAlternativeSellAndSwitchFlow(correlationId, ffsRequestBody, fundResponse);
            if(!StringUtils.isEmpty(fundResponse) && !fundResponse.isError()){
                fundResponse.setError(false);
                fundResponse.setErrorCode(ProductsExpServiceConstant.SUCCESS_CODE);
                fundResponse.setErrorMsg(ProductsExpServiceConstant.SUCCESS_MESSAGE);
                fundResponse.setErrorDesc(ProductsExpServiceConstant.SUCCESS);
            }
        }
        return fundResponse;
    }


    /**
     * To validate Alternative case and verify expire-citizen id
     *
     * @param ffsRequestBody
     * @param correlationId
     * @param ffsRsAndValidation
     * @return FfsRsAndValidation
     */
    @LogAround
    public FfsRsAndValidation validationAlternativeFlow(String correlationId, FfsRequestBody ffsRequestBody,
                                                        FfsRsAndValidation ffsRsAndValidation) {
        final boolean isNotValid = true;
        boolean isStoped = false;
        if(isCASADormant(correlationId, ffsRequestBody)){
            ffsRsAndValidation.setError(isNotValid);
            ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.CASA_DORMANT_ACCOUNT_CODE);
            ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.CASA_DORMANT_ACCOUNT_MESSAGE);
            ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.CASA_DORMANT_ACCOUNT_DESC);
            isStoped = true;
        }
        if(!isStoped && isSuitabilityExpired(correlationId, ffsRequestBody)){
            ffsRsAndValidation.setError(isNotValid);
            ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.SUITABILITY_EXPIRED_CODE);
            ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.SUITABILITY_EXPIRED_MESSAGE);
            ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.SUITABILITY_EXPIRED_DESC);
        }
        if(!isStoped && isCustIDExpired(ffsRequestBody)){
            ffsRsAndValidation.setError(isNotValid);
            ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.ID_EXPIRED_CODE);
            ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.ID_EXPIRED_MESSAGE);
            ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.ID_EXPIRED_DESC);
            isStoped = true;
        }
        if(!StringUtils.isEmpty(ffsRequestBody.getProcessFlag()) && isOfShelfFund(correlationId, ffsRequestBody)){
            ffsRsAndValidation.setError(isNotValid);
            ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.OF_SHELF_FUND_CODE);
            ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.OF_SHELF_FUND_MESSAGE);
            ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.OF_SHELF_FUND_DESC);
            isStoped = true;
        }
        if(!isStoped && isBusinessClose(correlationId, ffsRequestBody)){
            ffsRsAndValidation.setError(isNotValid);
            ffsRsAndValidation.setErrorCode(ProductsExpServiceConstant.BUSINESS_HOURS_CLOSE_CODE);
            ffsRsAndValidation.setErrorMsg(ProductsExpServiceConstant.BUSINESS_HOURS_CLOSE_MESSAGE);
            ffsRsAndValidation.setErrorDesc(ProductsExpServiceConstant.BUSINESS_HOURS_CLOSE_DESC);
        }
        return ffsRsAndValidation;
    }


    /**
     * To validate Alternative case and verify expire-citizen id
     *
     * @param ffsRequestBody
     * @param correlationId
     * @param fundResponse
     * @return FundResponse
     */
    @LogAround
    public FundResponse validationAlternativeSellAndSwitchFlow(String correlationId, FfsRequestBody ffsRequestBody,
                                                                     FundResponse fundResponse) {
        final boolean isNotValid = true;
        boolean isStoped = false;
        if(isSuitabilityExpired(correlationId, ffsRequestBody)){
            fundResponse.setError(isNotValid);
            fundResponse.setErrorCode(ProductsExpServiceConstant.SUITABILITY_EXPIRED_CODE);
            fundResponse.setErrorMsg(ProductsExpServiceConstant.SUITABILITY_EXPIRED_MESSAGE);
            fundResponse.setErrorDesc(ProductsExpServiceConstant.SUITABILITY_EXPIRED_DESC);
            isStoped = true;
        }
        if(!isStoped && isCustIDExpired(ffsRequestBody)){
            fundResponse.setError(isNotValid);
            fundResponse.setErrorCode(ProductsExpServiceConstant.ID_EXPIRED_CODE);
            fundResponse.setErrorMsg(ProductsExpServiceConstant.ID_EXPIRED_MESSAGE);
            fundResponse.setErrorDesc(ProductsExpServiceConstant.ID_EXPIRED_DESC);
        }
        return fundResponse;
    }

    /**
     * Method isServiceHour Query service hour from common-service
     *
     * @param correlationId
     * @param fundResponse
     */
    public FundResponse isServiceHour(String correlationId, FundResponse fundResponse){
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> responseCommon = null;
        try{
            responseCommon = commonServiceClient.getCommonConfigByModule(correlationId, ProductsExpServiceConstant.INVESTMENT_MODULE_VALUE);
            logger.info(ProductsExpServiceConstant.CUSTOMER_EXP_SERVICE_RESPONSE, responseCommon);
            if(!StringUtils.isEmpty(responseCommon)){
                List<CommonData> commonDataList = responseCommon.getBody().getData();
                CommonData commonData = commonDataList.get(0);
                CommonTime noneServiceHour = commonData.getNoneServiceHour();
                if(UtilMap.isBusinessClose(noneServiceHour.getStart(), noneServiceHour.getEnd())) {
                    fundResponse.setError(true);
                    fundResponse.setErrorCode(ProductsExpServiceConstant.SERVICE_OUR_CLOSE);
                    fundResponse.setErrorMsg(noneServiceHour.getStart());
                    fundResponse.setErrorDesc(noneServiceHour.getEnd());
                }
            }
            return fundResponse;
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return fundResponse;
        }
    }


    /*


     */

    /**
     * Method isOfShelfFund for get all fund list and check with fund code
     *
     * @param correlationId
     * @param ffsRequestBody
     */
    public boolean isOfShelfFund(String correlationId, FfsRequestBody ffsRequestBody){
        ResponseEntity<TmbOneServiceResponse<FundListPage>> responseResponseEntity = null;
        try{
            Map<String, Object> invHeaderReqParameter = UtilMap.createHeader(correlationId, 0, 0);
            responseResponseEntity = investmentRequestClient.callInvestmentFundListInfoService(invHeaderReqParameter);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseResponseEntity);
            if (!StringUtils.isEmpty(responseResponseEntity) &&
                    HttpStatus.OK == responseResponseEntity.getStatusCode()) {
                return UtilMap.isOfShelfCheck(ffsRequestBody, responseResponseEntity.getBody().getData().getFundClassList());
            }
            return true;
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return true;
        }
    }

    /**
     * Method isBusinessClose for check cut of time from fundRule
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
                return (UtilMap.isBusinessClose(fundRuleInfoList.getTimeStart(), fundRuleInfoList.getTimeEnd())
                        && ProductsExpServiceConstant.BUSINESS_HR_CLOSE.equals(fundRuleInfoList.getFundAllowOtx()));
            }
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return true;
        }
        return false;
    }


    /**
     * Method isCASADormant get Customer account and check dormant status
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
     * Method isSuitabilityExpired Call MF service to check suitability is expire.
     *
     * @param correlationId
     * @param ffsRequestBody
     */
    public boolean isSuitabilityExpired(String correlationId, FfsRequestBody ffsRequestBody){
        ResponseEntity<TmbOneServiceResponse<SuitabilityInfo>> responseResponseEntity = null;
        try{
            SuitabilityBody suitabilityBody = new SuitabilityBody();
            suitabilityBody.setRmNumber(ffsRequestBody.getCrmId());
            Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
            responseResponseEntity = investmentRequestClient.callInvestmentFundSuitabilityService(invHeaderReqParameter, suitabilityBody);
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseResponseEntity);
            return UtilMap.isSuitabilityExpire(responseResponseEntity.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return true;
        }
    }

    /**
     * Method isCustIDExpired call to customer-info and get id_expire_date to verify with current date
     *
     * @param ffsRequestBody
     */
    @LogAround
    public boolean isCustIDExpired(FfsRequestBody ffsRequestBody){
        ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>>  responseResponseEntity = null;
        try{
            Map<String, String> requestHeadersParameter = new HashMap<>();
            responseResponseEntity = customerServiceClient.getCustomerProfile(requestHeadersParameter, ffsRequestBody.getCrmId());
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseResponseEntity.getBody().getData().getIdNo());
            return UtilMap.isCustIDExpired(responseResponseEntity.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return true;
        }
    }




    /**
     * Method constructActivityLogDataForBuyHoldingFund
     *
     * @param correlationId
     * @param activityType
     * @param trackingStatus
     * @param alternativeRq
     */
    public ActivityLogs constructActivityLogDataForBuyHoldingFund(String correlationId,
                                                                  String activityType,
                                                                  String trackingStatus,
                                                                  AlternativeRq alternativeRq) {
        String failReason = alternativeRq.getProcessFlag().equals(ProductsExpServiceConstant.PROCESS_FLAG_Y) ?
                ProductsExpServiceConstant.SUCCESS_MESSAGE : ProductsExpServiceConstant.FAILED_MESSAGE ;


        ActivityLogs activityData = new ActivityLogs(correlationId, String.valueOf(System.currentTimeMillis()), trackingStatus);
        activityData.setActivityStatus(failReason);
        activityData.setChannel(ProductsExpServiceConstant.ACTIVITY_LOG_CHANNEL);
        activityData.setAppVersion(ProductsExpServiceConstant.ACTIVITY_LOG_APP_VERSION);
        activityData.setFailReason(failReason);
        activityData.setActivityType(activityType);
        activityData.setCrmId(alternativeRq.getCrmId());
        activityData.setVerifyFlag(alternativeRq.getProcessFlag());
        activityData.setReason(failReason);
        activityData.setFundCode(alternativeRq.getFundCode());
        if(!StringUtils.isEmpty(alternativeRq.getUnitHolderNo())){
            activityData.setUnitHolderNo(alternativeRq.getUnitHolderNo());
        }else{
            activityData.setUnitHolderNo(ProductsExpServiceConstant.UNIT_HOLDER);
        }
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
