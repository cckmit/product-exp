package com.tmb.oneapp.productsexpservice.controller.productexperience.customer;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.request.FatcaRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.response.FatcaResponseBody;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.FatcaService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FatcaControllerTest {

    @Mock
    private FatcaService fatcaService;

    @InjectMocks
    private FatcaController fatcaController;

    private String correlationId = "correlationId";

    private String crmId = "crmId";

    private String ipAddress = "0.0.0.0";

    @Test
    void should_return_status_success_when_call_create_fatca_form_given_status_success_form_service() {
        // Given
        TmbOneServiceResponse<FatcaResponseBody> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        FatcaRequest fatcaRequest = FatcaRequest.builder().build();
        when(fatcaService.createFatcaForm(correlationId, crmId, ipAddress, fatcaRequest)).thenReturn(oneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<FatcaResponseBody>> actual = fatcaController.createFatcaForm(correlationId, crmId, ipAddress, fatcaRequest);

        // Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(TmbStatusUtil.successStatus().getCode(), actual.getBody().getStatus().getCode());
    }

    @Test
    void should_return_status_not_found_when_call_create_fatca_form_given_status_not_found_form_service() {
        // Given
        TmbOneServiceResponse<FatcaResponseBody> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(TmbStatusUtil.notFoundStatus());
        FatcaRequest fatcaRequest = FatcaRequest.builder().build();
        when(fatcaService.createFatcaForm(correlationId, crmId, ipAddress, fatcaRequest)).thenReturn(oneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<FatcaResponseBody>> actual = fatcaController.createFatcaForm(correlationId, crmId, ipAddress, fatcaRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals(TmbStatusUtil.notFoundStatus().getCode(), actual.getBody().getStatus().getCode());
    }

    @Test
    void should_return_status_bad_request_when_call_create_fatca_form_given_status_bad_request_form_service() {
        // Given
        TmbOneServiceResponse<FatcaResponseBody> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setStatus(TmbStatusUtil.badRequestStatus());
        FatcaRequest fatcaRequest = FatcaRequest.builder().build();
        when(fatcaService.createFatcaForm(correlationId, crmId, ipAddress, fatcaRequest)).thenReturn(oneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<FatcaResponseBody>> actual = fatcaController.createFatcaForm(correlationId, crmId, ipAddress, fatcaRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertEquals(TmbStatusUtil.badRequestStatus().getCode(), actual.getBody().getStatus().getCode());
    }
}