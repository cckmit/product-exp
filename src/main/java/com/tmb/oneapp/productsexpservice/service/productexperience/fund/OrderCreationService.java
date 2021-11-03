package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.activitylog.transaction.service.EnterPinIsCorrectActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.enums.ActivityLogStatus;
import com.tmb.oneapp.productsexpservice.feignclients.CacheServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CreditCardDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.common.findbyfundhouse.FundHouseBankData;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.processfirsttrade.ProcessFirstTradeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Account;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Card;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Merchant;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
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

    @Autowired
    public OrderCreationService(CacheServiceClient cacheServiceClient,
                                InvestmentRequestClient investmentRequestClient,
                                EnterPinIsCorrectActivityLogService enterPinIsCorrectActivityLogService,
                                CreditCardClient creditCardClient,
                                CommonServiceClient commonServiceClient) {
        this.cacheServiceClient = cacheServiceClient;
        this.investmentRequestClient = investmentRequestClient;
        this.enterPinIsCorrectActivityLogService = enterPinIsCorrectActivityLogService;
        this.creditCardClient = creditCardClient;
        this.commonServiceClient = commonServiceClient;
    }

    @LogAround
    public TmbOneServiceResponse<OrderCreationPaymentResponse> makeTransaction(String correlationId,
                                                                               String crmId,
                                                                               OrderCreationPaymentRequestBody request) throws TMBCommonException {
        TmbOneServiceResponse<OrderCreationPaymentResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());

        try {
            Map<String, String> investmentRequestHeader = UtilMap.createHeaderWithCrmId(correlationId, crmId);
            String pin = ProductsExpServiceConstant.INVESTMENT_VERIFY_PIN_REF_ID + request.getRefId();
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

            if (checkDuplicateTransaction(correlationId, request.getOrderType(), request.getRefId(), pinCacheData)) {
                tmbOneServiceResponse.getStatus().setCode(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_CODE);
                tmbOneServiceResponse.getStatus().setDescription(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_MESSAGE);
                tmbOneServiceResponse.getStatus().setMessage(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_MESSAGE);
                tmbOneServiceResponse.getStatus().setService(ProductsExpServiceConstant.SERVICE_NAME);
                return tmbOneServiceResponse;
            }

            pushDataToRedis(correlationId, request.getOrderType(), request.getRefId());
            ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> response =
                    processOrderPayment(correlationId, investmentRequestHeader, request);
            postOrderActivityPayment(correlationId, crmId, investmentRequestHeader, request, response);
            tmbOneServiceResponse.setData(response.getBody().getData());

        } catch (FeignException feignException) {
            handleFeignException(feignException);
        } catch (Exception ex) {
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
        return tmbOneServiceResponse;
    }

    private ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> processOrderPayment(String correlationId, Map<String, String> investmentRequestHeader, OrderCreationPaymentRequestBody request) throws TMBCommonException, JsonProcessingException {

        ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> response;
        if (ProductsExpServiceConstant.PURCHASE_TRANSACTION_LETTER_TYPE.equals(request.getOrderType())) {
            Account toAccount = getAccount(correlationId, request);
            request.setToAccount(toAccount);
            // buy flow
            if (request.isCreditCard()) {
                // credit card
                ResponseEntity<FetchCardResponse> cardResponse = creditCardClient.getCreditCardDetails(correlationId, request.getFromAccount().getAccountId());
                CreditCardDetail creditCard = cardResponse.getBody().getCreditCard();
                request.setCard(Card.builder()
                        .cardId(creditCard.getCardId())
                        .cardExpiry(creditCard.getCardInfo().getExpiredBy())
                        .cardEmbossingName(creditCard.getCardInfo().getCardEmbossingName1())
                        .productId(creditCard.getProductId())
                        .productGroupId(ProductsExpServiceConstant.INVESTMENT_CREDIT_CARD_GROUP_ID)
                        .build());
                String merchantId = ProductsExpServiceConstant.INVESTMENT_FUND_CLASS_CODE_LTF_MERCHANT
                        .equals(request.getFundClassCode()) ? toAccount.getLtfMerchantId() : toAccount.getRmfMerchantId();
                request.setMerchant(Merchant.builder().merchantId(merchantId).build());
                logger.info("createOrderPayment buy flow credit card request casa obj : {}", UtilMap.convertObjectToStringJson(request));
                response = investmentRequestClient.createOrderPayment(investmentRequestHeader, request);
            } else {
                // casa account
                logger.info("createOrderPayment buy flow casa request casa obj : {}", UtilMap.convertObjectToStringJson(request));
                response = investmentRequestClient.createOrderPayment(investmentRequestHeader, request);
            }

        } else {
            // sell or switch flow
            if (ProductsExpServiceConstant.FULL_REDEEM.equalsIgnoreCase(request.getRedeemType())) {
                request.setFullRedemption(ProductsExpServiceConstant.REVERSE_FLAG_Y);
            }

            if (ProductsExpServiceConstant.AMOUNT_TYPE_IN_PARTIAL_SERVICE.equalsIgnoreCase(request.getRedeemType())) {
                request.setRedeemType(ProductsExpServiceConstant.AMOUNT_TYPE_IN_ORDER_SERVICE);
            }

            logger.info("createOrderPayment sell or switch request casa obj : {}", UtilMap.convertObjectToStringJson(request));
            response = investmentRequestClient.createOrderPayment(investmentRequestHeader, request);
        }

        return response;
    }

    private void postOrderActivityPayment(String correlationId, String crmId, Map<String, String> investmentRequestHeader, OrderCreationPaymentRequestBody request, ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> response) {
        String activityLogStatus = "";
        if (ProductsExpServiceConstant.SUCCESS_CODE.equalsIgnoreCase(response.getBody().getStatus().getCode())) {
            activityLogStatus = ActivityLogStatus.SUCCESS.getStatus();
            saveOrderPayment(investmentRequestHeader, response.getBody(), request);
            processFirstTrade(investmentRequestHeader, request, response.getBody().getData());
            enterPinIsCorrectActivityLogService.save(correlationId, crmId, request, activityLogStatus, response.getBody().getData(), request.getOrderType());
        } else {
            activityLogStatus = ActivityLogStatus.FAILED.getStatus();
            enterPinIsCorrectActivityLogService.save(correlationId, crmId, request, activityLogStatus, null, request.getOrderType());
        }
    }

    private void processFirstTrade(Map<String, String> investmentRequestHeader,
                                   OrderCreationPaymentRequestBody request,
                                   OrderCreationPaymentResponse response) {
        try {
            ProcessFirstTradeRequestBody processFirstTradeRequestBody = ProcessFirstTradeRequestBody.builder()
                    .portfolioNumber(request.getPortfolioNumber())
                    .fundHouseCode(request.getFundHouseCode())
                    .fundCode(request.getFundCode())
                    .orderId(response.getOrderId())
                    .effectiveDate(response.getEffectiveDate())
                    .build();
            logger.info("processFirstTrade request casa obj : {}", processFirstTradeRequestBody);
            ResponseEntity<TmbOneServiceResponse<String>> processFirstTradeResponse = investmentRequestClient.processFirstTrade(investmentRequestHeader, processFirstTradeRequestBody);

            logger.info("finish sending request to processFirstTrade with {} status  ", processFirstTradeResponse.getBody().getStatus().getCode());

        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
    }

    private void saveOrderPayment(Map<String, String> investmentRequestHeader, TmbOneServiceResponse<OrderCreationPaymentResponse> body, OrderCreationPaymentRequestBody request) {
        try {
            OrderCreationPaymentResponse response = body.getData();

            SaveOrderCreationRequestBody saveOrderCreationRequestBody = SaveOrderCreationRequestBody.builder()
                    .fundCode(request.getFundCode())
                    .portfolioNumber(request.getPortfolioNumber())
                    .orderAmount(request.getOrderAmount())
                    .orderId(response.getOrderId())
                    .paymentObject(response.getPaymentObject())
                    .build();
            logger.info("saveOrderPayment request casa obj : {}", UtilMap.convertObjectToStringJson(saveOrderCreationRequestBody));
            ResponseEntity<TmbOneServiceResponse<String>> saveOrderResponse = investmentRequestClient.saveOrderPayment(investmentRequestHeader, saveOrderCreationRequestBody);

            logger.info("finish sending request to save order payment with {} status  ", saveOrderResponse.getBody().getStatus().getCode());

        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
    }

    /***
     * Set Account in request. Getting account value from common service
     * @param correlationId
     * @param bodyRequest
     * @return
     * @throws JsonProcessingException
     */
    private Account getAccount(String correlationId, OrderCreationPaymentRequestBody bodyRequest) throws JsonProcessingException, TMBCommonException {
        try {

            ResponseEntity<TmbOneServiceResponse<FundHouseBankData>> fundHouseResponse = commonServiceClient.fetchBankInfoByFundHouse(correlationId,
                    bodyRequest.getFundHouseCode());
            FundHouseBankData fundHouseResponseData = fundHouseResponse.getBody().getData();
            logger.info("searching toAccount by fund house code " + bodyRequest.getFundHouseCode());
            logger.info("Response : {} ", TMBUtils.convertJavaObjectToString(fundHouseResponse));

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

    /**
     * Push data to radis in order to check duplicate transaction
     *
     * @param correlationId
     * @param orderType
     * @param refId
     */
    private void pushDataToRedis(String correlationId, String orderType, String refId) {
        CacheModel redisRequest = new CacheModel();
        redisRequest.setKey(orderType + refId);
        redisRequest.setTtl(ProductsExpServiceConstant.TTL_REDIS);
        redisRequest.setValue(ProductsExpServiceConstant.TRUE);
        Map<String, String> headerParameter = UtilMap.createHeader(correlationId);
        cacheServiceClient.putCacheByKey(headerParameter, redisRequest);
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
                                              String pinCacheData) {

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
