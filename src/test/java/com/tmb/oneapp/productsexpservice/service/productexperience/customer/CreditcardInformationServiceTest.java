package com.tmb.oneapp.productsexpservice.service.productexperience.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.response.CreditcardInformationResponse;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreditcardInformationServiceTest {

    @InjectMocks
    public CreditcardInformationService creditcardInformationService;

    @Mock
    private TMBLogger<CreditcardInformationServiceTest> logger;

    @Mock
    CustomerExpServiceClient customerExpServiceClient;

    @Test
    void should_return_creditcard_information_when_call_get_creditcard_information_given_correlation_id_and_crmid() throws IOException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";


        TmbOneServiceResponse<CreditcardInformationResponse> tmbFundsummaryResponse = new TmbOneServiceResponse<>();
        CreditcardInformationResponse creditcardInformationResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/creditcardinformation.json").toFile(), CreditcardInformationResponse.class);
        tmbFundsummaryResponse.setData(creditcardInformationResponse);
        when(customerExpServiceClient.getCustomerCreditCard(any(),
                any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbFundsummaryResponse));

        //When
        TmbOneServiceResponse<CreditcardInformationResponse> actual = creditcardInformationService.getCredicardInformation(correlationId,crmId);

        //Then
        assertEquals(TmbStatusUtil.successStatus().getCode(),actual.getStatus().getCode());
        assertEquals(creditcardInformationResponse, actual.getData());
    }

    @Test
    void should_return_null_when_call_get_creditcard_information_given_correlation_id_and_crmid() throws JsonProcessingException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";;

        when(customerExpServiceClient.getCustomerCreditCard(any(),
                any())).thenThrow(new RuntimeException("Error"));

        //When
        TmbOneServiceResponse<CreditcardInformationResponse> actual = creditcardInformationService.getCredicardInformation(correlationId,crmId);

        //Then
        assertNull(actual.getStatus());
        assertNull(actual.getData());
    }

}
