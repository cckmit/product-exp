package com.tmb.oneapp.productsexpservice.service.productexperience.customer;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.activitylog.fatca.service.FatcaActivityLogService;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.request.FatcaRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.response.FatcaResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FatcaServiceTest {

    private TMBLogger<FatcaServiceTest> logger = new TMBLogger<>(FatcaServiceTest.class);

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerExpServiceClient customerExpServiceClient;

    @Mock
    private FatcaActivityLogService fatcaActivityLogService;

    @InjectMocks
    private FatcaService fatcaService;

    private String correlationId = "correlationId";

    private String crmId = "001100000000000000000001184383";

    private String ipAddress = "0.0.0.0";

    @Test
    void should_return_fatca_request_with_status_success_when_call_create_fatca_form_given_status_success_form_customer_services() {
        // Given
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse.builder().build();
        customerSearchResponse.setFatcaFlag("flag");
        when(customerService.getCustomerInfo(any(), any())).thenReturn(customerSearchResponse);

        FatcaRequest fatcaRequest = FatcaRequest.builder().build();
        TmbOneServiceResponse<FatcaResponseBody> fatcaResponse = new TmbOneServiceResponse<>();
        fatcaResponse.setStatus(TmbStatusUtil.successStatus());
        ResponseEntity<TmbOneServiceResponse<FatcaResponseBody>> response = new ResponseEntity<>(
                fatcaResponse, HttpStatus.OK);
        when(customerExpServiceClient.createFatcaForm(correlationId, crmId, fatcaRequest)).thenReturn(response);

        // When
        TmbOneServiceResponse<FatcaResponseBody> actual = fatcaService.createFatcaForm(correlationId, crmId, ipAddress, fatcaRequest);

        // Then
        TmbStatus tmbStatus = TmbStatusUtil.successStatus();
        assertEquals(tmbStatus.getCode(), actual.getStatus().getCode());
        verify(fatcaActivityLogService, times(1)).clickNextButtonAtFatcaQuestionScreen(anyString(), anyString(), anyString(), anyString(), any());
    }

    @Test
    void should_return_fatca_request_with_status_bad_request_when_call_create_fatca_form_given_return_null_form_customer_services() {
        // Given
        when(customerService.getCustomerInfo(any(), any())).thenReturn(null);

        FatcaRequest fatcaRequest = FatcaRequest.builder().build();

        // When
        TmbOneServiceResponse<FatcaResponseBody> actual = fatcaService.createFatcaForm(correlationId, crmId, ipAddress, fatcaRequest);

        // Then
        TmbStatus tmbStatus = TmbStatusUtil.badRequestStatus();
        assertEquals(tmbStatus.getCode(), actual.getStatus().getCode());
        assertEquals("error fetch customer search", actual.getStatus().getDescription());
        verify(fatcaActivityLogService, times(1)).clickNextButtonAtFatcaQuestionScreen(anyString(), anyString(), anyString(), anyString(), any());
    }

    @Test
    void should_return_fatca_request_with_status_bad_request_when_call_create_fatca_form_given_throw_exception_form_customer_services() {
        // Given
        when(customerService.getCustomerInfo(any(), any())).thenThrow(MockitoException.class);

        FatcaRequest fatcaRequest = FatcaRequest.builder().build();

        // When
        TmbOneServiceResponse<FatcaResponseBody> actual = fatcaService.createFatcaForm(correlationId, crmId, ipAddress, fatcaRequest);

        // Then
        TmbStatus tmbStatus = TmbStatusUtil.badRequestStatus();
        assertEquals(tmbStatus.getCode(), actual.getStatus().getCode());
        verify(fatcaActivityLogService, times(1)).clickNextButtonAtFatcaQuestionScreen(anyString(), anyString(), anyString(), anyString(), any());
    }

    @Test
    void should_return_fatca_request_with_status_success_when_call_create_fatca_form_given_throw_exception_form_investment_services() {
        // Given
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse.builder().build();
        customerSearchResponse.setFatcaFlag("flag");
        when(customerService.getCustomerInfo(anyString(), anyString())).thenReturn(customerSearchResponse);

        FatcaRequest fatcaRequest = FatcaRequest.builder().build();
        when(customerExpServiceClient.createFatcaForm(correlationId, crmId, fatcaRequest)).thenThrow(MockitoException.class);

        // When
        TmbOneServiceResponse<FatcaResponseBody> actual = fatcaService.createFatcaForm(correlationId, crmId, ipAddress, fatcaRequest);

        // Then
        TmbStatus tmbStatus = TmbStatusUtil.badRequestStatus();
        assertEquals(tmbStatus.getCode(), actual.getStatus().getCode());
        verify(fatcaActivityLogService, times(1)).clickNextButtonAtFatcaQuestionScreen(anyString(), anyString(), anyString(), anyString(), any());
    }
}