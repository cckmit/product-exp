package com.tmb.oneapp.productsexpservice.activitylog.transaction.service;

import com.tmb.oneapp.productsexpservice.activitylog.buy.service.BuyActivityLogService;
import com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.service.SellActivityLogService;
import com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.service.SwitchActivityLogService;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnterPinIsCorrectActivityLogServiceTest {

    @Mock
    private BuyActivityLogService buyActivityLogService;

    @Mock
    private SellActivityLogService sellActivityLogService;

    @Mock
    private SwitchActivityLogService switchActivityLogService;

    @InjectMocks
    private EnterPinIsCorrectActivityLogService enterPinIsCorrectActivityLogService;

    @Test
    void should_call_buy_activity_log_service_when_call_save_given_order_type_p() {
        // Given
        OrderCreationPaymentRequestBody paymentRequestBody = new OrderCreationPaymentRequestBody();
        OrderCreationPaymentResponse paymentResponseBody = new OrderCreationPaymentResponse();

        // When
        enterPinIsCorrectActivityLogService.save("correlationId", "crmId", paymentRequestBody, "completed", paymentResponseBody, "P");

        // Then
        verify(buyActivityLogService, times(1)).enterEnterPinIsCorrect(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void should_call_sell_activity_log_service_when_call_save_given_order_type_r() {
        // Given
        OrderCreationPaymentRequestBody paymentRequestBody = new OrderCreationPaymentRequestBody();
        OrderCreationPaymentResponse paymentResponseBody = new OrderCreationPaymentResponse();

        // When
        enterPinIsCorrectActivityLogService.save("correlationId", "crmId", paymentRequestBody, "completed", paymentResponseBody, "R");

        // Then
        verify(sellActivityLogService, times(1)).enterEnterPinIsCorrect(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void should_call_switch_activity_log_service_when_call_save_given_order_type_s() {
        // Given
        OrderCreationPaymentRequestBody paymentRequestBody = new OrderCreationPaymentRequestBody();
        OrderCreationPaymentResponse paymentResponseBody = new OrderCreationPaymentResponse();

        // When
        enterPinIsCorrectActivityLogService.save("correlationId", "crmId", paymentRequestBody, "completed", paymentResponseBody, "S");

        // Then
        verify(switchActivityLogService, times(1)).enterEnterPinIsCorrect(anyString(), anyString(), anyString(), any(), any());
    }
}