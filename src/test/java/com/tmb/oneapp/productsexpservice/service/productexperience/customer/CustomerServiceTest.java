package com.tmb.oneapp.productsexpservice.service.productexperience.customer;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    public CustomerExpServiceClient customerExpServiceClient;

    @Mock
    public CustomerServiceClient customerServiceClient;

    @InjectMocks
    public CustomerService customerService;

    @Test
    void should_return_customer_info_when_call_getCustomerInfo_given_correlation_id_and_crm_id() throws IOException {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";
        CustomerSearchResponse.builder().crmId(crmId).build();
        TmbOneServiceResponse<List<CustomerSearchResponse>> tmbOneServiceResponse = new TmbOneServiceResponse();
        tmbOneServiceResponse.setData(List.of(CustomerSearchResponse.builder().crmId(crmId).build()));
        when(customerServiceClient.customerSearch(any(),any(),any())).thenReturn(ResponseEntity.ok(tmbOneServiceResponse));

        //When
        CustomerSearchResponse actual = customerService.getCustomerInfo(correlationId,crmId);

        //Then
        assertEquals(crmId,actual.getCrmId());
    }

    @Test
    void should_return_account_saving_when_call_get_account_saving_given_correlation_id_and_crm_id() throws IOException {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";
        String accounts = "01010101,1111111";
        when(customerExpServiceClient.getAccountSaving(any(),any())).thenReturn(accounts);

        //When
        String actual = customerService.getAccountSaving(correlationId,crmId);

        //Then
        assertEquals(accounts,actual);
    }

}
