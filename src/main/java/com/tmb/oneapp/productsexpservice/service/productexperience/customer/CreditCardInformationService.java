package com.tmb.oneapp.productsexpservice.service.productexperience.customer;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.response.CreditCard;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.response.CreditCardInformationResponse;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CreditCardInformationService get credit information
 */
@Service
public class CreditCardInformationService {

    private static final TMBLogger<CreditCardInformationService> logger = new TMBLogger<>(CreditCardInformationService.class);

    private final CustomerExpServiceClient customerExpServiceClient;

    @Autowired
    public CreditCardInformationService(CustomerExpServiceClient customerExpServiceClient) {
        this.customerExpServiceClient = customerExpServiceClient;
    }

    /**
     * Method getCreditCardInformation to call customer exp service get credit
     *
     * @param correlationId
     * @param crmId
     * @return CreditCardInformationResponse
     */
    public TmbOneServiceResponse<CreditCardInformationResponse> getCreditCardInformation(String correlationId, String crmId) {
        TmbOneServiceResponse<CreditCardInformationResponse> response = new TmbOneServiceResponse<>();
        try {
            ResponseEntity<TmbOneServiceResponse<CreditCardInformationResponse>> creditCardInformationResponse =
                    customerExpServiceClient.getCustomerCreditCard(correlationId, crmId);

            if (!creditCardInformationResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new TMBCommonException("failed call customer-exp-service for get credit card");
            }

            List<CreditCard> creditCards = creditCardInformationResponse.getBody().getData().getCreditCards();
            return filterCreditCardWithStatusAndType(creditCards, response);
        } catch (Exception ex) {
            logger.info("error : {}", ex);
            response.setStatus(null);
            response.setData(null);
            return response;
        }
    }

    private TmbOneServiceResponse<CreditCardInformationResponse> filterCreditCardWithStatusAndType(List<CreditCard> creditCards, TmbOneServiceResponse<CreditCardInformationResponse> response) {
        CreditCardInformationResponse creditcardInformationResponse = new CreditCardInformationResponse();
        creditcardInformationResponse.setCreditCards(creditCards.stream().filter(t -> t.getAccountStatus().equals("active") && !t.getCardType().equals("SUP")).collect(Collectors.toList()));
        response.setStatus(TmbStatusUtil.successStatus());
        response.setData(creditcardInformationResponse);
        return response;
    }
}
