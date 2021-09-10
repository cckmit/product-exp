package com.tmb.oneapp.productsexpservice.controller.productexperience.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.CreditCardInformationRequestBody;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.response.CreditCardInformationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CreditCardInformationService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreditCardInformationControllerTest {

    @InjectMocks
    public CreditCardInformationController creditcardInformationController;

    @Mock
    public CreditCardInformationService creditcardInformationService;

    @Test
    void should_return_credit_card_information_when_call_get_credit_card_information_given_correlation_id_and_crm_id() throws IOException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";

        CreditCardInformationResponse creditcardInformationResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/credit_card_information.json").toFile(),
                CreditCardInformationResponse.class);
        TmbOneServiceResponse tmbOneServiceResponse = new TmbOneServiceResponse();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        tmbOneServiceResponse.setData(creditcardInformationResponse);

        when(creditcardInformationService.getCreditCardInformation(correlationId, crmId)).thenReturn(tmbOneServiceResponse);

        //When
        ResponseEntity<TmbOneServiceResponse<CreditCardInformationResponse>> actual =
                creditcardInformationController.getCreditCardInformation(correlationId, crmId);
        //Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(creditcardInformationResponse, actual.getBody().getData());

    }

    @Test
    void should_return_not_found_when_call_get_dca_information_given_correlation_id_and_crm_id() {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";

        TmbOneServiceResponse tmbOneServiceResponse = new TmbOneServiceResponse();
        tmbOneServiceResponse.setStatus(null);
        tmbOneServiceResponse.setData(null);

        when(creditcardInformationService.getCreditCardInformation(correlationId, crmId)).thenReturn(tmbOneServiceResponse);

        //When
        ResponseEntity<TmbOneServiceResponse<CreditCardInformationResponse>> actual =
                creditcardInformationController.getCreditCardInformation(correlationId, crmId);
        //Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals(TmbStatusUtil.notFoundStatus().getCode(), actual.getBody().getStatus().getCode());
        assertNull(actual.getBody().getData());
    }
}
