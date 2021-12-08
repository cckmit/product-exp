package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.activitylog.transaction.service.EnterPinIsCorrectActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.*;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CreditCardDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.common.findbyfundhouse.FundHouseBankData;
import com.tmb.oneapp.productsexpservice.model.productexperience.financial.saveactivity.request.SaveActivityRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.financial.sync.request.FinancalSyncRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.processfirsttrade.ProcessFirstTradeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Account;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Card;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Merchant;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderConfirmPayment;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.saveordercreation.SaveOrderCreationRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.cache.CacheModel;
import com.tmb.oneapp.productsexpservice.service.productexperience.TmbErrorHandle;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderCreationService extends TmbErrorHandle {

    private static final TMBLogger<OrderCreationService> logger = new TMBLogger<>(OrderCreationService.class);

    private final CacheServiceClient cacheServiceClient;

    private final InvestmentRequestClient investmentRequestClient;

    private final EnterPinIsCorrectActivityLogService enterPinIsCorrectActivityLogService;

    private final CreditCardClient creditCardClient;

    private final CommonServiceClient commonServiceClient;

    private final FinancialServiceClient financialServiceClient;

    @Autowired
    public OrderCreationService(CacheServiceClient cacheServiceClient,
                                InvestmentRequestClient investmentRequestClient,
                                EnterPinIsCorrectActivityLogService enterPinIsCorrectActivityLogService,
                                CreditCardClient creditCardClient,
                                CommonServiceClient commonServiceClient,
                                FinancialServiceClient financialServiceClient) {
        this.cacheServiceClient = cacheServiceClient;
        this.investmentRequestClient = investmentRequestClient;
        this.enterPinIsCorrectActivityLogService = enterPinIsCorrectActivityLogService;
        this.creditCardClient = creditCardClient;
        this.commonServiceClient = commonServiceClient;
        this.financialServiceClient = financialServiceClient;
    }

    /***
     * Generic method to make transaction for order creation payment
     * @param correlationId
     * @param crmId
     * @param ipAddress
     * @param requestBody
     * @return OrderCreationPaymentResponse
     */
    @LogAround
    public TmbOneServiceResponse<OrderCreationPaymentResponse> makeTransaction(String correlationId,
                                                                               String crmId,
                                                                               String ipAddress,
                                                                               OrderCreationPaymentRequestBody requestBody) throws TMBCommonException {
        TmbOneServiceResponse<OrderCreationPaymentResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());

        try {
            Map<String, String> investmentRequestHeader = UtilMap.createHeaderWithCrmId(correlationId, crmId);
            String pin = ProductsExpServiceConstant.INVESTMENT_VERIFY_PIN_REF_ID + requestBody.getRefId();
            ResponseEntity<TmbOneServiceResponse<String>> pinVerifyData = cacheServiceClient.getCacheByKey(correlationId, pin);
            String pinCacheData = pinVerifyData.getBody().getData();

            logger.info("pin >>> " + pin);
            logger.info("key >>> " + pinCacheData);

            if (StringUtils.isEmpty(pinCacheData)) {
                tmbOneServiceResponse.getStatus().setCode(ProductsExpServiceConstant.INVESTMENT_PIN_INVALID_CODE);
                tmbOneServiceResponse.getStatus().setDescription(ProductsExpServiceConstant.INVESTMENT_PIN_INVALID_MSG);
                tmbOneServiceResponse.getStatus().setMessage(ProductsExpServiceConstant.INVESTMENT_PIN_INVALID_MSG);
                tmbOneServiceResponse.getStatus().setService(ProductsExpServiceConstant.SERVICE_NAME);
                return tmbOneServiceResponse;
            }

            if (checkDuplicateTransaction(correlationId, requestBody.getOrderType(), requestBody.getRefId(), pinCacheData)) {
                tmbOneServiceResponse.getStatus().setCode(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_CODE);
                tmbOneServiceResponse.getStatus().setDescription(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_MESSAGE);
                tmbOneServiceResponse.getStatus().setMessage(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_MESSAGE);
                tmbOneServiceResponse.getStatus().setService(ProductsExpServiceConstant.SERVICE_NAME);
                return tmbOneServiceResponse;
            }

            pushDataToRedis(correlationId, requestBody.getOrderType(), requestBody.getRefId());
            ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> response =
                    processOrderPayment(correlationId, investmentRequestHeader, requestBody);
            postOrderActivityPayment(correlationId, crmId, ipAddress, investmentRequestHeader, requestBody, response);
            tmbOneServiceResponse.setData(response.getBody().getData());

        } catch (FeignException feignException) {
            TmbStatus tmbStatus = buildFeignException(feignException);
            if (tmbStatus != null) {
                TmbOneServiceResponse<OrderCreationPaymentResponse> oneServiceResponse = new TmbOneServiceResponse();
                oneServiceResponse.setStatus(tmbStatus);
                oneServiceResponse.setData(OrderCreationPaymentResponse.builder().build());
                enterPinIsCorrectActivityLogService.save(correlationId, crmId, ipAddress, requestBody, oneServiceResponse);
            }

            handleFeignException(feignException);
        } catch (Exception ex) {
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
        return tmbOneServiceResponse;
    }

    /***
     * Method to check duplicated of transaction
     * @param correlationId
     * @param orderType
     * @param refId
     * @param pinCacheData
     * @return Boolean
     */
    @LogAround
    private boolean checkDuplicateTransaction(String correlationId, String orderType, String refId, String pinCacheData) {

        ResponseEntity<TmbOneServiceResponse<String>> pinVerifyData = cacheServiceClient.getCacheByKey(correlationId, orderType + refId);
        String pinData = pinVerifyData.getBody().getData();

        return (ProductsExpServiceConstant.TRUE.equalsIgnoreCase(pinCacheData))
                && (!StringUtils.isEmpty(pinData))
                && ProductsExpServiceConstant.TRUE.equalsIgnoreCase(pinData);
    }

    /**
     * Method to push data into redis in order to check duplicated of transaction
     *
     * @param correlationId
     * @param orderType
     * @param refId
     * @return
     */
    @LogAround
    private void pushDataToRedis(String correlationId, String orderType, String refId) {
        CacheModel redisRequest = new CacheModel();
        redisRequest.setKey(orderType + refId);
        redisRequest.setTtl(ProductsExpServiceConstant.TTL_REDIS);
        redisRequest.setValue(ProductsExpServiceConstant.TRUE);
        Map<String, String> headerParameter = UtilMap.createHeader(correlationId);
        cacheServiceClient.putCacheByKey(headerParameter, redisRequest);
    }

    /***
     * Method to precess order payment
     * @param correlationId
     * @param investmentRequestHeader
     * @param requestBody
     * @return Boolean
     */
    @LogAround
    private ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> processOrderPayment(String correlationId,
                                                                                                    Map<String, String> investmentRequestHeader,
                                                                                                    OrderCreationPaymentRequestBody requestBody) throws TMBCommonException, JsonProcessingException {
        ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> response;
        if (ProductsExpServiceConstant.PURCHASE_TRANSACTION_LETTER_TYPE.equals(requestBody.getOrderType())) {
            Account toAccount = getAccount(correlationId, requestBody);
            requestBody.setToAccount(toAccount);
            // buy flow
            if (requestBody.isCreditCard()) {
                // credit card
                ResponseEntity<FetchCardResponse> cardResponse = creditCardClient.getCreditCardDetails(correlationId, requestBody.getFromAccount().getAccountId());
                CreditCardDetail creditCard = cardResponse.getBody().getCreditCard();
                requestBody.setCard(Card.builder()
                        .cardId(creditCard.getCardId())
                        .cardExpiry(creditCard.getCardInfo().getExpiredBy())
                        .cardEmbossingName(creditCard.getCardInfo().getCardEmbossingName1())
                        .productId(creditCard.getProductId())
                        .productGroupId(ProductsExpServiceConstant.INVESTMENT_CREDIT_CARD_GROUP_ID)
                        .build());
                String merchantId = ProductsExpServiceConstant.INVESTMENT_FUND_CLASS_CODE_LTF_MERCHANT
                        .equals(requestBody.getFundClassCode()) ? toAccount.getLtfMerchantId() : toAccount.getRmfMerchantId();
                requestBody.setMerchant(Merchant.builder().merchantId(merchantId).build());
                logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, ProductsExpServiceConstant.INVESTMENT_ORDER_CREATION_API, "buy flow creditcard requestBody"), UtilMap.convertObjectToStringJson(requestBody));
                response = investmentRequestClient.createOrderPayment(investmentRequestHeader, requestBody);
                logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, ProductsExpServiceConstant.INVESTMENT_ORDER_CREATION_API, "buy flow creditcard response"), UtilMap.convertObjectToStringJson(response));

            } else {
                // casa account
                logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, ProductsExpServiceConstant.INVESTMENT_ORDER_CREATION_API, "buy flow casa requestBody"), UtilMap.convertObjectToStringJson(requestBody));
                response = investmentRequestClient.createOrderPayment(investmentRequestHeader, requestBody);
                logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, ProductsExpServiceConstant.INVESTMENT_ORDER_CREATION_API, "buy flow casa response"), UtilMap.convertObjectToStringJson(response));

            }

        } else {
            // sell or switch flow
            if (ProductsExpServiceConstant.FULL_REDEEM.equalsIgnoreCase(requestBody.getRedeemType())) {
                requestBody.setFullRedemption(ProductsExpServiceConstant.REVERSE_FLAG_Y);
            }

            if (ProductsExpServiceConstant.AMOUNT_TYPE_IN_PARTIAL_SERVICE.equalsIgnoreCase(requestBody.getRedeemType())) {
                requestBody.setRedeemType(ProductsExpServiceConstant.AMOUNT_TYPE_IN_ORDER_SERVICE);
            }

            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, ProductsExpServiceConstant.INVESTMENT_ORDER_CREATION_API, "sell or switch requestBody"), UtilMap.convertObjectToStringJson(requestBody));
            response = investmentRequestClient.createOrderPayment(investmentRequestHeader, requestBody);
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, ProductsExpServiceConstant.INVESTMENT_ORDER_CREATION_API, "sell or switch response"), UtilMap.convertObjectToStringJson(response));

        }
        return response;
    }

    /***
     * Method to set an account in request, then get the account value from common service
     * @param correlationId
     * @param requestBody
     * @return Account
     */
    @LogAround
    private Account getAccount(String correlationId, OrderCreationPaymentRequestBody requestBody) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "findbyfundhousecode", ProductsExpServiceConstant.LOGGING_REQUEST), requestBody.getFundHouseCode());
            ResponseEntity<TmbOneServiceResponse<FundHouseBankData>> fundHouseResponse = commonServiceClient.fetchBankInfoByFundHouse(correlationId,
                    requestBody.getFundHouseCode());
            FundHouseBankData fundHouseResponseData = fundHouseResponse.getBody().getData();
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "findbyfundhousecode", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(fundHouseResponseData));

            Account toAccount = new Account();
            Optional<FundHouseBankData> fundHouseBankData = Optional.ofNullable(fundHouseResponseData);
            if (fundHouseBankData.isPresent()) {
                FundHouseBankData data = fundHouseBankData.get();
                toAccount.setAccountId(data.getToAccountNo());
                toAccount.setAccountType(data.getAccountType());
                toAccount.setFiId(data.getFinancialId());
                toAccount.setLtfMerchantId(data.getLtfMerchantId());
                toAccount.setRmfMerchantId(data.getRmfMerchantId());
            }
            return toAccount;
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage() + "fet account error",
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }

    /***
     * Method to post order activity payment
     * @param correlationId
     * @param crmId
     * @param ipAddress
     * @param investmentRequestHeader
     * @param requestBody
     * @param response
     * @return
     */
    @LogAround
    private void postOrderActivityPayment(String correlationId, String crmId, String ipAddress,
                                          Map<String, String> investmentRequestHeader,
                                          OrderCreationPaymentRequestBody requestBody,
                                          ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> response) {
        if (ProductsExpServiceConstant.SUCCESS_CODE.equalsIgnoreCase(response.getBody().getStatus().getCode())) {
            String transactionDate = String.valueOf(Instant.now().toEpochMilli());
            syncLogActivityToOneAppCalendar(correlationId, crmId, transactionDate, requestBody, response);
            saveLogActivityToOneAppCalendar(correlationId, crmId, transactionDate, requestBody, response);
            saveOrderPayment(investmentRequestHeader, response.getBody(), requestBody);
            processFirstTrade(investmentRequestHeader, requestBody, response.getBody().getData());
        }
        enterPinIsCorrectActivityLogService.save(correlationId, crmId, ipAddress, requestBody, response.getBody());
    }

    /***
     * Method to sync application log to OneApp calendar
     * @param correlationId
     * @param crmId
     * @param requestBody
     * @param response
     * @return
     */
    @LogAround
    private void syncLogActivityToOneAppCalendar(String correlationId, String crmId, String transactionDate,
                                                 OrderCreationPaymentRequestBody requestBody,
                                                 ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> response) {
        try {
            OrderCreationPaymentResponse orderCreationPaymentResponse = response.getBody().getData();
            OrderConfirmPayment orderConfirmPayment = orderCreationPaymentResponse.getPaymentObject();
            FinancalSyncRequest financalSyncRequest = FinancalSyncRequest.builder()
                    .referenceId(orderConfirmPayment.getPaymentId())
                    .crmId(UtilMap.fullCrmIdFormat(crmId))
                    .transactionDate(transactionDate)
                    .fromAccountNo(orderConfirmPayment.getFromAccount().getAccountId())
                    .fromAccountName(requestBody.getAccountName())
                    .toAccountNo(orderConfirmPayment.getToAccount().getAccountId())
                    .toAccountName(requestBody.getFundHouseCode())
                    .bankcode("11")
                    .transactionAmount(requestBody.getOrderAmount())
                    .transactionFee(orderConfirmPayment.getFee().getPaymentFee())
                    .activityTypeId("024")
                    .fromAccountType(orderConfirmPayment.getFromAccount().getAccountType())
                    .toAccountType(orderConfirmPayment.getToAccount().getAccountType())
                    .channelId(orderConfirmPayment.getPaymentChannel())
                    .transactionBalance(orderConfirmPayment.getAccount().getLedgerBal())
                    .transactionStatus("success")
                    .activityTypeIdNew("101000105")
                    .errorCd("0000")
                    .activityRefId(orderConfirmPayment.getRequestId())
                    .txnType("001")
                    .build();
            financialServiceClient.syncData(correlationId, financalSyncRequest);
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED + " financialServiceClient syncData", ex);
        }
    }

    /***
     * Method to save application log to OneApp calendar
     * @param correlationId
     * @param crmId
     * @param requestBody
     * @param response
     * @return
     */
    @LogAround
    private void saveLogActivityToOneAppCalendar(String correlationId, String crmId, String transactionDate,
                                                 OrderCreationPaymentRequestBody requestBody,
                                                 ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> response) {
        try {
            OrderCreationPaymentResponse orderCreationPaymentResponse = response.getBody().getData();
            OrderConfirmPayment orderConfirmPayment = orderCreationPaymentResponse.getPaymentObject();
            SaveActivityRequest saveActivityRequest = SaveActivityRequest.builder()
                    .referenceActivityTypeId("000")
                    .activityTypeId("024")
                    .crmId(UtilMap.fullCrmIdFormat(crmId))
                    .channelId(orderConfirmPayment.getPaymentChannel())
                    .transactionStatus("success")
                    .transactionDate(transactionDate)
                    .fromAccountNo(orderConfirmPayment.getFromAccount().getAccountId())
                    .toAccountNo(orderConfirmPayment.getToAccount().getAccountId())
                    .toAccountNickname(String.format("%s%s", requestBody.getFundHouseCode(), requestBody.getFundCode()))
                    .toAccountName(requestBody.getFundHouseCode())
                    .financialTranferAmount(requestBody.getOrderAmount())
                    .financialTranferCrDr("2")
                    .financialTranferRefId(orderConfirmPayment.getPaymentId())
                    .build();
            financialServiceClient.saveActivity(correlationId, saveActivityRequest);
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED + " financialServiceClient saveData", ex);
        }
    }

    /***
     * Method to save order payment to MF service
     * @param investmentRequestHeader
     * @param serviceResponse
     * @param requestBody
     * @return
     */
    @LogAround
    private void saveOrderPayment(Map<String, String> investmentRequestHeader,
                                  TmbOneServiceResponse<OrderCreationPaymentResponse> serviceResponse,
                                  OrderCreationPaymentRequestBody requestBody) {
        try {
            OrderCreationPaymentResponse orderCreationPaymentResponse = serviceResponse.getData();

            SaveOrderCreationRequestBody request = SaveOrderCreationRequestBody.builder()
                    .fundCode(requestBody.getFundCode())
                    .portfolioNumber(requestBody.getPortfolioNumber())
                    .orderAmount(requestBody.getOrderAmount())
                    .orderId(orderCreationPaymentResponse.getOrderId())
                    .paymentObject(orderCreationPaymentResponse.getPaymentObject())
                    .build();

            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "saveOrderPayment", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.convertObjectToStringJson(request));
            ResponseEntity<TmbOneServiceResponse<String>> response = investmentRequestClient.saveOrderPayment(investmentRequestHeader, request);
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "saveOrderPayment", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(response));

        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
    }

    /***
     * Method to process order first trade
     * @param investmentRequestHeader
     * @param requestBody
     * @param response
     * @return
     */
    @LogAround
    private void processFirstTrade(Map<String, String> investmentRequestHeader, OrderCreationPaymentRequestBody requestBody, OrderCreationPaymentResponse response) {
        try {
            ProcessFirstTradeRequestBody processFirstTradeRequestBody = ProcessFirstTradeRequestBody.builder()
                    .portfolioNumber(requestBody.getPortfolioNumber())
                    .fundHouseCode(requestBody.getFundHouseCode())
                    .fundCode(requestBody.getFundCode())
                    .orderId(response.getOrderId())
                    .effectiveDate(response.getEffectiveDate())
                    .build();

            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "processFirstTrade", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.convertObjectToStringJson(processFirstTradeRequestBody));
            ResponseEntity<TmbOneServiceResponse<String>> processFirstTradeResponse = investmentRequestClient.processFirstTrade(investmentRequestHeader, processFirstTradeRequestBody);
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT, "processFirstTrade", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(processFirstTradeResponse));

        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
    }
}
