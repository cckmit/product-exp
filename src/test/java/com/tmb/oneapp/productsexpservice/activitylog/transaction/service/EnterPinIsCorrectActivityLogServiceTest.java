package com.tmb.oneapp.productsexpservice.activitylog.transaction.service;

import com.tmb.common.model.TmbOneServiceResponse;
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
        OrderCreationPaymentRequestBody requestBody = new OrderCreationPaymentRequestBody();
        requestBody.setOrderType("P");
        TmbOneServiceResponse<OrderCreationPaymentResponse> response = new TmbOneServiceResponse<>();

        // When
        enterPinIsCorrectActivityLogService.save("correlationId", "crmId", "0.0.0.0", requestBody, response);

        // Then
        verify(buyActivityLogService, times(1)).enterEnterPinIsCorrect(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void should_call_sell_activity_log_service_when_call_save_given_order_type_r() {
        // Given
        OrderCreationPaymentRequestBody requestBody = new OrderCreationPaymentRequestBody();
        requestBody.setOrderType("R");
        TmbOneServiceResponse<OrderCreationPaymentResponse> response = new TmbOneServiceResponse<>();

        // When
        enterPinIsCorrectActivityLogService.save("correlationId", "crmId", "0.0.0.0", requestBody, response);

        // Then
        verify(sellActivityLogService, times(1)).enterEnterPinIsCorrect(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void should_call_switch_activity_log_service_when_call_save_given_order_type_s() {
        // Given
        OrderCreationPaymentRequestBody requestBody = new OrderCreationPaymentRequestBody();
        requestBody.setOrderType("S");
        TmbOneServiceResponse<OrderCreationPaymentResponse> response = new TmbOneServiceResponse<>();

        // When
        enterPinIsCorrectActivityLogService.save("correlationId", "crmId", "0.0.0.0", requestBody, response);

        // Then
        verify(switchActivityLogService, times(1)).enterEnterPinIsCorrect(anyString(), anyString(), anyString(), any(), any());
    }
}