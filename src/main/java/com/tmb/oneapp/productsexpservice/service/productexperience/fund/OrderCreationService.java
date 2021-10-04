package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.creditcard.GetCardsBalancesResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.activitylog.transaction.service.EnterPinIsCorrectActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.ActivityLogStatus;
import com.tmb.oneapp.productsexpservice.enums.AlternativeOpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.CacheServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Account;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import com.tmb.oneapp.productsexpservice.model.request.cache.CacheModel;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Service
public class OrderCreationService {

    private static final TMBLogger<OrderCreationService> logger = new TMBLogger<>(OrderCreationService.class);

    private final CacheServiceClient cacheServiceClient;

    private final InvestmentRequestClient investmentRequestClient;

    private final EnterPinIsCorrectActivityLogService enterPinIsCorrectActivityLogService;

    private final CreditCardClient creditCardClient;

    @Autowired
    public OrderCreationService(CacheServiceClient cacheServiceClient,
                                InvestmentRequestClient investmentRequestClient,
                                EnterPinIsCorrectActivityLogService enterPinIsCorrectActivityLogService,
                                CreditCardClient creditCardClient) {
        this.cacheServiceClient = cacheServiceClient;
        this.investmentRequestClient = investmentRequestClient;
        this.enterPinIsCorrectActivityLogService = enterPinIsCorrectActivityLogService;
        this.creditCardClient = creditCardClient;
    }

    @LogAround
    public TmbOneServiceResponse<OrderCreationPaymentResponse> makeTransaction(String correlationId,
                                                                               String crmId,
                                                                               OrderCreationPaymentRequestBody request){
        TmbOneServiceResponse<OrderCreationPaymentResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());

        Map<String, String> investmentRequestHeader = UtilMap.createHeaderWithCrmId(correlationId,crmId);
        String pin = ProductsExpServiceConstant.INVESTMENT_VERIFY_PIN_REF_ID + request.getRefId();
        ResponseEntity<TmbOneServiceResponse<String>> pinVerifyData = cacheServiceClient.getCacheByKey(correlationId,pin);
        String pinCacheData = pinVerifyData.getBody().getData();

        logger.info("pin >>> " + pin);
        logger.info("key >>> {} " + pinCacheData);

        if (StringUtils.isEmpty(pinCacheData)) {
            tmbOneServiceResponse.getStatus().setCode(ProductsExpServiceConstant.INVESTMENT_PIN_INVALID_CODE);
            tmbOneServiceResponse.getStatus().setDescription(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getDesc());
            tmbOneServiceResponse.getStatus().setMessage(ProductsExpServiceConstant.INVESTMENT_PIN_INVALID_MSG);
            tmbOneServiceResponse.getStatus().setService(ProductsExpServiceConstant.SERVICE_NAME);
            return tmbOneServiceResponse;
        }

        if(checkDuplicateTransaction(correlationId,request.getOrderType(),request.getRefId(),pinCacheData)){
            tmbOneServiceResponse.getStatus().setCode(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_CODE);
            tmbOneServiceResponse.getStatus().setDescription(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_MES);
            tmbOneServiceResponse.getStatus().setMessage(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_MES);
            tmbOneServiceResponse.getStatus().setService(ProductsExpServiceConstant.SERVICE_NAME);
            return tmbOneServiceResponse;
        }

        ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> response = null;

        if(ProductsExpServiceConstant.PURCHASE_TRANSACTION_LETTER_TYPE.equals(request.getOrderType())){
            // buy flow
            if(request.isCreditCard()){
                // creditcard
                ResponseEntity<FetchCardResponse> cardResponse = creditCardClient.getCreditCardDetails(correlationId,request.getFromAccount().getAccountId());

            }else{
                // casa account
                Account toAccount = getAccount(requestBody, commonServiceHeader);
                request.setToAccount(toAccount);
                response = investmentRequestClient.createOrderPayment(investmentRequestHeader,request);
            }

        }else{
            // sell or switch flow
            if (ProductsExpServiceConstant.FULL_REDEEM.equalsIgnoreCase(request.getRedeemType())) {
                request.setFullRedemption(ProductsExpServiceConstant.REVERSE_FLAG_Y);
            }

            if (ProductsExpServiceConstant.AMOUNT_TYPE_IN_PARTIAL_SERVICE.equalsIgnoreCase(request.getRedeemType())) {
                request.setRedeemType(ProductsExpServiceConstant.AMOUNT_TYPE_IN_ORDER_SERVICE);
            }

            pushDataToRedis(correlationId,request.getOrderType(),request.getRefId());
            response = investmentRequestClient.createOrderPayment(investmentRequestHeader,request);
        }

        String activityLogStatus = "";
        if (ProductsExpServiceConstant.SUCCESS_CODE.equalsIgnoreCase(response.getBody().getStatus().getCode())) {
            activityLogStatus = ActivityLogStatus.SUCCESS.getStatus();
//            saveConfirmResponse(response.getBody(), request.getOrderAmount());
//            processFirstTrade(request, response.getBody(), correlationId);
            enterPinIsCorrectActivityLogService.save(correlationId, crmId, request, activityLogStatus, response.getBody().getData(), request.getOrderType());
        } else {
            activityLogStatus = ActivityLogStatus.FAILED.getStatus();
            enterPinIsCorrectActivityLogService.save(correlationId, crmId, request, activityLogStatus, null, request.getOrderType());
        }

        tmbOneServiceResponse.setData(response.getBody().getData());
        return tmbOneServiceResponse;
    }

    /***
     * Set Account in request. Getting account value from common service
     * @param bodyRequest
     * @param headerForCommonService
     * @return
     * @throws JsonProcessingException
     */
//    private Account getAccount(OrderCreationPaymentRequestBody bodyRequest, Map<String, String> headerForCommonService) throws JsonProcessingException {
//        FundHouseResponse fundHouseResponse = commonServiceClient.fetchBankInfoByFundHouse(headerForCommonService,
//                bodyRequest.getFundHouseCode());
//
//        logger.info("searching toAccount by fund house code " + bodyRequest.getFundHouseCode());
//
//        Account toAccount = new Account();
//        Optional<FundHouseBankData> fundHouseBankData = Optional.ofNullable(fundHouseResponse.getData());
//        if (fundHouseBankData.isPresent()) {
//            logger.info("Response from DB {} ", TMBUtils.convertJavaObjectToString(fundHouseResponse));
//
//            toAccount.setAccountId(fundHouseResponse.getData().getToAccountNo());
//            toAccount.setAccountType(fundHouseResponse.getData().getAccountType());
//            toAccount.setFiId(fundHouseResponse.getData().getFinancialId());
//        }
//        return toAccount;
//    }

    /**
     * Push data to radis in order to check duplicate transaction
     *
     * @param correlationId
     * @param orderType
     * @param refId
     */
    private void pushDataToRedis(String correlationId,String orderType,String refId) {
        CacheModel redisRequest = new CacheModel();
        redisRequest.setKey(orderType + refId);
        redisRequest.setTtl(ProductsExpServiceConstant.TTL_REDIS);
        redisRequest.setValue(ProductsExpServiceConstant.TRUE);
        Map<String, String> headerParameter = UtilMap.createHeader(correlationId);
        cacheServiceClient.putCacheByKey(headerParameter,redisRequest);
    }

    /***
     * Check duplicate transaction
     * @param correlationId
     * @param orderType
     * @param refId
     * @param pinCacheData
     */
    private boolean checkDuplicateTransaction(String correlationId,
                                              String orderType,
                                              String refId,
                                              String pinCacheData){

        boolean isDuplicateTransaction = false;
        ResponseEntity<TmbOneServiceResponse<String>> pinVerifyData = cacheServiceClient.getCacheByKey(correlationId,
                orderType + refId);

        String pinData = pinVerifyData.getBody().getData();

        if ((ProductsExpServiceConstant.TRUE.equalsIgnoreCase(pinCacheData))
                && (!StringUtils.isEmpty(pinData))
                && ProductsExpServiceConstant.TRUE.equalsIgnoreCase(pinData)) {

            isDuplicateTransaction = true;
            return isDuplicateTransaction;

        }
        return isDuplicateTransaction;
    }



}
