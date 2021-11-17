package com.tmb.oneapp.productsexpservice.service.productexperience.customer;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.response.CreditCard;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.response.CreditCardInformationResponse;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * CreditCardInformationService get credit information
 */
@Service
public class CreditCardInformationService {

    private static final TMBLogger<CreditCardInformationService> logger = new TMBLogger<>(CreditCardInformationService.class);

    private final CustomerExpServiceClient customerExpServiceClient;

    private final CommonServiceClient commonServiceFeignClient;

    @Autowired
    public CreditCardInformationService(CustomerExpServiceClient customerExpServiceClient,
                                        CommonServiceClient commonServiceFeignClient) {
        this.customerExpServiceClient = customerExpServiceClient;
        this.commonServiceFeignClient = commonServiceFeignClient;
    }

    /**
     * Method getCreditCardInformation to call customer exp service get credit
     *
     * @param correlationId
     * @param crmId
     * @return CreditCardInformationResponse
     */
    @LogAround
    public TmbOneServiceResponse<CreditCardInformationResponse> getCreditCardInformation(String correlationId, String crmId) {
        TmbOneServiceResponse<CreditCardInformationResponse> response = new TmbOneServiceResponse<>();
        try {
            CompletableFuture<List<ProductConfig>> fetchProductConfigs = fetchAyncProductConfig(correlationId);
            CompletableFuture<CreditCardInformationResponse> creditCardInformation = fetchAyncCustomerCreditCard(correlationId,UtilMap.fullCrmIdFormat(crmId));
            CompletableFuture.allOf(fetchProductConfigs, creditCardInformation);

            CreditCardInformationResponse creditCardInformationResponse = creditCardInformation.get();
            List<ProductConfig> productConfigs = fetchProductConfigs.get();
            List<CreditCard> creditCards = creditCardInformationResponse.getCreditCards();
            return filterCreditCardWithStatusAndType(productConfigs,creditCards, response);

        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
            response.setStatus(null);
            response.setData(null);
            return response;
        }
    }

    @Async
    @LogAround
    public CompletableFuture<CreditCardInformationResponse> fetchAyncCustomerCreditCard(String correlationId,String crmId) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<CreditCardInformationResponse>> response =
                    customerExpServiceClient.getCustomerCreditCard(correlationId, crmId);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (Exception e) {
            logger.error("Error getCustomerCreditCard", e);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }

    @Async
    @LogAround
    public CompletableFuture<List<ProductConfig>> fetchAyncProductConfig(String correlationId) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> response = commonServiceFeignClient.getProductConfig(correlationId);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (Exception e) {
            logger.error("Error fetchProductConfig", e);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }

    @LogAround
    private TmbOneServiceResponse<CreditCardInformationResponse> filterCreditCardWithStatusAndType(List<ProductConfig> productConfigList, List<CreditCard> creditCards, TmbOneServiceResponse<CreditCardInformationResponse> response) {
        CreditCardInformationResponse creditcardInformationResponse = new CreditCardInformationResponse();
        List<CreditCard> creditCardWithFilterStatusAndType = creditCards.stream()
                .filter(t -> t.getAccountStatus()
                        .equals(ProductsExpServiceConstant.INVESTMENT_CREDIT_CARD_ACTIVE_STATUS) &&
                        !t.getCardType().equals(ProductsExpServiceConstant.INVESTMENT_CREDIT_CARD_SUP_TYPE))
                .collect(Collectors.toList());

        List<CreditCard> creditCardEligiblePurchaseMutualFund =
                filterAllowPurchaseMfAndAccountTypeIsCCA(productConfigList,creditCardWithFilterStatusAndType);
        creditcardInformationResponse.setCreditCards(creditCardEligiblePurchaseMutualFund);

        response.setStatus(TmbStatusUtil.successStatus());
        response.setData(creditcardInformationResponse);
        return response;
    }

    @LogAround
    private List<CreditCard> filterAllowPurchaseMfAndAccountTypeIsCCA(List<ProductConfig> productConfigList, List<CreditCard> creditCardWithFilterStatusAndType) {
        List<CreditCard> filterCreditCardResult = new ArrayList<>();
        for (CreditCard creditCard: creditCardWithFilterStatusAndType) {
            Optional<ProductConfig> productConfigOptional = productConfigList.stream()
                    .filter(pc -> "1".equals(pc.getAllowToPurchaseMf()) &&
                            ProductsExpServiceConstant.ACC_TYPE_CCA.equals(pc.getAccountType()) &&
                            pc.getProductCode().equals(creditCard.getProductCode()))
                    .findFirst();
            if(productConfigOptional.isPresent()){
                filterCreditCardResult.add(creditCard);
            }
        }
        return filterCreditCardResult;
    }

}
