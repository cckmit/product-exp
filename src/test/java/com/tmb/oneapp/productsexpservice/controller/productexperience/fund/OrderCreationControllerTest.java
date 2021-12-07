package com.tmb.oneapp.productsexpservice.controller.productexperience.fund;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.fund.OrderCreationService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderCreationControllerTest {

    @Mock
    private OrderCreationService orderCreationService;

    @InjectMocks
    private OrderCreationController orderCreationController;

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private final String crmId = "001100000000000000000001184383";

    @Test
    void should_return_success_when_call_order_creation_payment_with_correlationId_and_crm_id_and_order_creation_request_body() throws TMBCommonException {
        // Given
        TmbOneServiceResponse<OrderCreationPaymentResponse> response = new TmbOneServiceResponse<>();
        response.setStatus(TmbStatusUtil.successStatus());
        when(orderCreationService.makeTransaction(any(), any(), any())).thenReturn(response);

        // When
        ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> actual =
                orderCreationController.orderCreationPayment(correlationId, crmId, OrderCreationPaymentRequestBody.builder().build());

        // Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getBody().getStatus().getCode());
    }

    @Test
    void should_return_not_found_status_when_call_order_creation_payment_with_correlationId_and_crm_id_and_order_creation_request_body() throws TMBCommonException {
        // Given
        TmbOneServiceResponse<OrderCreationPaymentResponse> response = new TmbOneServiceResponse<>();
        response.setStatus(null);
        when(orderCreationService.makeTransaction(any(), any(), any())).thenReturn(response);

        // When
        ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> actual =
                orderCreationController.orderCreationPayment(correlationId, crmId, OrderCreationPaymentRequestBody.builder().build());

        // Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE, actual.getBody().getStatus().getCode());
    }
}
