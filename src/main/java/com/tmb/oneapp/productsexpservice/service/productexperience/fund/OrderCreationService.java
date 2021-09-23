package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeOpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.CacheServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.OrderRequestClient;
import com.tmb.oneapp.productsexpservice.mapper.ordercreation.OrderCreationMapper;
import com.tmb.oneapp.productsexpservice.model.productexperience.mutualfund.HeaderRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.SellAndSwitchRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.SellAndSwitchRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import com.tmb.oneapp.productsexpservice.model.request.cache.CacheModel;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class OrderCreationService {

    private static final TMBLogger<OrderCreationService> logger = new TMBLogger<>(OrderCreationService.class);

    private final CacheServiceClient cacheServiceClient;

    private final OrderRequestClient orderRequestClient;

    private final OrderCreationMapper orderCreationMapper;

    public OrderCreationService(CacheServiceClient cacheServiceClient, OrderRequestClient orderRequestClient, OrderCreationMapper orderCreationMapper) {
        this.cacheServiceClient = cacheServiceClient;
        this.orderRequestClient = orderRequestClient;
        this.orderCreationMapper = orderCreationMapper;
    }

    @LogAround
    public TmbOneServiceResponse<OrderCreationPaymentResponse> makeTransaction(String correlationId,
                                                                               String crmId,
                                                                               OrderCreationPaymentRequestBody request){
        TmbOneServiceResponse<OrderCreationPaymentResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());

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

        if(ProductsExpServiceConstant.INVESTMENT_PURCHASE_TRANSACTION_LETTER_TYPE.equals(request.getOrderType())){

            if(request.isCreditCard()){

            }else{

            }

        }else{

            if (ProductsExpServiceConstant.FULL_REDEEM.equalsIgnoreCase(request.getRedeemType())) {
                request.setFullRedemption(ProductsExpServiceConstant.REVERSE_FLAG_Y);
            }

            if (ProductsExpServiceConstant.AMOUNT_TYPE_IN_PARTIAL_SERVICE.equalsIgnoreCase(request.getRedeemType())) {
                request.setRedeemType(ProductsExpServiceConstant.AMOUNT_TYPE_IN_ORDER_SERVICE);
            }

            pushDataToRedis(correlationId,request.getOrderType(),request.getRefId());
            responseData = createSellOrSwitchTransaction(header, request);
        }

        return tmbOneServiceResponse;
    }

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

    /**
     * Create Sell or Switch transaction base on orderType
     *
     * @param bodyRequest
     * @param header
     * @return
     */
    private OrderCreationPaymentResponse createSellOrSwitchTransaction(HeaderRequest header, OrderCreationPaymentRequestBody bodyRequest) {
        SellAndSwitchRequest sellAndSwitchRequest = new SellAndSwitchRequest();
        SellAndSwitchRequestBody sellAndSwitchRequestBody = orderCreationMapper.orderCreationBodyToSellAndSwtichRequestBody(bodyRequest);
        sellAndSwitchRequest.setBody(sellAndSwitchRequestBody);
        return  orderRequestClient.createSellAndSwitchTransaction(sellAndSwitchRequest);
    }

}
