package com.tmb.oneapp.productsexpservice.service.productexperience.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.response.CreditCardInformationResponse;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreditCardInformationServiceTest {

    @InjectMocks
    public CreditCardInformationService creditcardInformationService;

    @Mock
    private TMBLogger<CreditCardInformationServiceTest> logger;

    @Mock
    private CustomerExpServiceClient customerExpServiceClient;

    @Mock
    private CommonServiceClient commonServiceFeignClient;

    @Test
    void should_return_credit_card_information_when_call_get_credit_card_information_given_correlation_id_and_crm_id() throws IOException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";


        TmbOneServiceResponse<CreditCardInformationResponse> tmbFundSummaryResponse = new TmbOneServiceResponse<>();
        CreditCardInformationResponse creditcardInformationResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/credit_card_information.json").toFile(), CreditCardInformationResponse.class);
        tmbFundSummaryResponse.setData(creditcardInformationResponse);
        when(customerExpServiceClient.getCustomerCreditCard(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbFundSummaryResponse));

        ProductConfig productConfig = new ProductConfig();
        productConfig.setProductCode("VABSIN");
        productConfig.setAllowToPurchaseMf("1");
        productConfig.setAccountType("CCA");
        List<ProductConfig> productConfigList = new ArrayList<>();
        productConfigList.add(productConfig);
        TmbOneServiceResponse tmbOneServiceResponse = new TmbOneServiceResponse();
        tmbOneServiceResponse.setData(productConfigList);
        when(commonServiceFeignClient.getProductConfig(any())).thenReturn(ResponseEntity.ok(tmbOneServiceResponse));

        //When
        TmbOneServiceResponse<CreditCardInformationResponse> actual = creditcardInformationService.getCreditCardInformation(correlationId,crmId);

        //Then
        assertEquals(TmbStatusUtil.successStatus().getCode(),actual.getStatus().getCode());
        assertEquals(creditcardInformationResponse, actual.getData());
    }

    @Test
    void should_return_null_when_call_get_credit_card_information_given_correlation_id_and_crm_id() {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";;

        when(commonServiceFeignClient.getProductConfig(any())).thenThrow(new RuntimeException("Error"));

        //When
        TmbOneServiceResponse<CreditCardInformationResponse> actual = creditcardInformationService.getCreditCardInformation(correlationId,crmId);

        //Then
        assertNull(actual.getStatus());
        assertNull(actual.getData());
    }
}
