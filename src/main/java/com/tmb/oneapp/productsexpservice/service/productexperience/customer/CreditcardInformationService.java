package com.tmb.oneapp.productsexpservice.service.productexperience.customer;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.response.CreditCard;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.response.CreditcardInformationResponse;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CreditcardInformationService get credit information
 */
@Service
public class CreditcardInformationService {

    private static final TMBLogger<CreditcardInformationService> logger = new TMBLogger<>(CreditcardInformationService.class);

    private final CustomerExpServiceClient customerExpServiceClient;

    @Autowired
    public CreditcardInformationService(CustomerExpServiceClient customerExpServiceClient) {
        this.customerExpServiceClient = customerExpServiceClient;
    }

    /**
     * Method getCredicardInformation to call customer exp service get credit
     *
     * @param correlationId
     * @param crmId
     * @return CreditcardInformationResponse
     */
    public TmbOneServiceResponse<CreditcardInformationResponse> getCredicardInformation(String correlationId, String crmId){
        TmbOneServiceResponse<CreditcardInformationResponse> response = new TmbOneServiceResponse<>();
        try {
            ResponseEntity<TmbOneServiceResponse<CreditcardInformationResponse>> creditCardInformationResponse =
                    customerExpServiceClient.getCustomerCreditCard(correlationId,crmId);
            if(!creditCardInformationResponse.getStatusCode().equals(HttpStatus.OK)){
                throw new TMBCommonException("failed call customer-exp-service for get credit card");
            }
            List<CreditCard> creditCards = creditCardInformationResponse.getBody().getData().getCreditCards();
            return filterCreditCardWithStatusAndType(creditCards,response);
        }catch (Exception ex){
            logger.info("error : {}",ex);
            response.setStatus(null);
            response.setData(null);
            return response;
        }
    }

    private TmbOneServiceResponse<CreditcardInformationResponse> filterCreditCardWithStatusAndType(List<CreditCard> creditCards, TmbOneServiceResponse<CreditcardInformationResponse> response) {
        CreditcardInformationResponse creditcardInformationResponse = new CreditcardInformationResponse();
        creditcardInformationResponse.setCreditCards(creditCards.stream().filter(t -> t.getAccountStatus().equals("active") && !t.getCardType().equals("SUP")).collect(Collectors.toList()));
        response.setStatus(TmbStatusUtil.successStatus());
        response.setData(creditcardInformationResponse);
        return response;
    }
}
