package com.tmb.oneapp.productsexpservice.controller.productexperience.transaction;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.orderaip.request.OrderAIPRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.orderaip.response.OrderAIPResponseBody;
import com.tmb.oneapp.productsexpservice.service.productexperience.transaction.AipService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AipControllerTest {

    @InjectMocks
    private AipController aipController;

    @Mock
    private AipService aipService;

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private final String crmId = "001100000000000000000012035644";

    private final String ipAddress = "0.0.0.0";

    @Test
    void should_return_success_status_when_call_create_aip_order_given_correlation_id_and_crm_id_and_ip_address_and_order_aip_request() throws TMBCommonException {
        // Given
        TmbOneServiceResponse<OrderAIPResponseBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        when(aipService.createAipOrder(anyString(), anyString(), anyString(), any())).thenReturn(tmbOneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<OrderAIPResponseBody>> actual =
                aipController.createAPIOrder(correlationId, crmId, ipAddress, OrderAIPRequestBody.builder().build());

        // Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getBody().getStatus().getCode());
    }

    @Test
    void should_return_not_found_status_when_call_create_aip_order_given_correlation_id_and_crm_id_and_ip_address_and_order_aip_request() throws TMBCommonException {
        // Given
        TmbOneServiceResponse<OrderAIPResponseBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(null);
        tmbOneServiceResponse.setData(null);
        when(aipService.createAipOrder(anyString(), anyString(), anyString(), any())).thenReturn(tmbOneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<OrderAIPResponseBody>> actual =
                aipController.createAPIOrder(correlationId, crmId, ipAddress, OrderAIPRequestBody.builder().build());

        // Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE, actual.getBody().getStatus().getCode());
    }
}
