package com.tmb.oneapp.productsexpservice.service.productexperience.transaction;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CreditCardDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.orderaip.request.OrderAIPRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.orderaip.response.OrderAIPResponseBody;
import com.tmb.oneapp.productsexpservice.service.productexperience.TmbErrorHandle;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class AipService extends TmbErrorHandle {

    private static final TMBLogger<AipService> logger = new TMBLogger<>(AipService.class);

    private final InvestmentRequestClient investmentRequestClient;

    private final CreditCardClient creditCardClient;

    @Autowired
    public AipService(InvestmentRequestClient investmentRequestClient, CreditCardClient creditCardClient) {
        this.investmentRequestClient = investmentRequestClient;
        this.creditCardClient = creditCardClient;
    }

    /**
     * Method createAipOrder
     *
     * @param correlationId
     * @param crmId
     * @param orderAIPRequestBody
     */
    @LogAround
    public TmbOneServiceResponse<OrderAIPResponseBody> createAipOrder(String correlationId, String crmId, OrderAIPRequestBody orderAIPRequestBody) throws TMBCommonException {
        TmbOneServiceResponse<OrderAIPResponseBody> tmbOneServiceResponse = new TmbOneServiceResponse();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        try {

            // credit card replace expiry date
            if("C".equals(orderAIPRequestBody.getBankAccountType())){
                logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_CREDIT_CARD,"getCreditCardDetails", ProductsExpServiceConstant.LOGGING_REQUEST),  orderAIPRequestBody.getBankAccountId());
                ResponseEntity<FetchCardResponse> cardResponse = creditCardClient.getCreditCardDetails(correlationId, orderAIPRequestBody.getBankAccountId());
                logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_CREDIT_CARD,"getCreditCardDetails", ProductsExpServiceConstant.LOGGING_RESPONSE),  UtilMap.convertObjectToStringJson(cardResponse.getBody()));

                CreditCardDetail creditCard = cardResponse.getBody().getCreditCard();
                String creditCardExpiry = creditCard.getCardInfo().getExpiredBy();
                if(StringUtils.isEmpty(creditCardExpiry))
                    throw new TMBCommonException("creditCardExpiry is empty");
                orderAIPRequestBody.setCreditCardExpiry(creditCardExpiry);
            }

            Map<String, String> investmentRequestHeader = UtilMap.createHeaderWithCrmId(correlationId, crmId);
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"createAipOrder", ProductsExpServiceConstant.LOGGING_REQUEST),  UtilMap.convertObjectToStringJson(orderAIPRequestBody));
            ResponseEntity<TmbOneServiceResponse<OrderAIPResponseBody>> oneServiceResponseResponseEntity = investmentRequestClient.createAipOrder(investmentRequestHeader,orderAIPRequestBody);
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"createAipOrder", ProductsExpServiceConstant.LOGGING_RESPONSE),  UtilMap.convertObjectToStringJson(oneServiceResponseResponseEntity.getBody()));

            tmbOneServiceResponse.setData(oneServiceResponseResponseEntity.getBody().getData());

        } catch (FeignException feignException) {
            handleFeignException(feignException);
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
        }
        return tmbOneServiceResponse;
    }

}
