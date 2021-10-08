package com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.service;

import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SwitchActivityLogServiceTest {

    @Mock
    private LogActivityService logActivityService;

    @InjectMocks
    private SwitchActivityLogService switchActivityLogService;

    @Test
    void should_call_create_log_when_call_enter_pin_is_correct_given_correlation_id_and_crm_id_and_status_and_payment_request_body_and_payment_response_body_not_null() {
        // Given
        OrderCreationPaymentRequestBody paymentRequestBody = new OrderCreationPaymentRequestBody();
        paymentRequestBody.setFullRedemption("Y");
        paymentRequestBody.setOrderUnit("10");
        OrderCreationPaymentResponse paymentResponseBody = new OrderCreationPaymentResponse();
        paymentResponseBody.setOrderId("orderId");

        // When
        switchActivityLogService.enterEnterPinIsCorrect("1234567890", "00000018592884", "completed",
                paymentRequestBody, paymentResponseBody);

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_enter_pin_is_correct_given_correlation_id_and_crm_id_and_status_and_payment_request_body_and_payment_response_body_null() {
        // Given
        OrderCreationPaymentRequestBody paymentRequestBody = new OrderCreationPaymentRequestBody();
        paymentRequestBody.setFullRedemption("U");
        paymentRequestBody.setRedeemType("A");
        paymentRequestBody.setOrderAmount("10");

        // When
        switchActivityLogService.enterEnterPinIsCorrect("1234567890", "00000018592884", "completed",
                paymentRequestBody, null);

        // Then
        verify(logActivityService).createLog(any());
    }
}