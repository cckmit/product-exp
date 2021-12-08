package com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.service;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.tmb.oneapp.productsexpservice.activitylog.util.ActivityStatusUtil.buildFailedStatus;
import static com.tmb.oneapp.productsexpservice.activitylog.util.ActivityStatusUtil.buildSuccessStatus;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwitchActivityLogServiceTest {

    @Mock
    private LogActivityService logActivityService;

    @InjectMocks
    private SwitchActivityLogService switchActivityLogService;

    @Test
    void should_call_create_log_when_call_enter_pin_is_correct_given_correlation_id_and_crm_id_and_status_and_ip_address_and_creation_request_body_and_creation_response_not_null() {
        // Given
        OrderCreationPaymentRequestBody requestBody = new OrderCreationPaymentRequestBody();
        requestBody.setFullRedemption("Y");
        requestBody.setOrderUnit("10");

        OrderCreationPaymentResponse creationPaymentResponse = new OrderCreationPaymentResponse();
        creationPaymentResponse.setOrderId("orderId");
        TmbOneServiceResponse<OrderCreationPaymentResponse> response = new TmbOneServiceResponse<>();
        response.setData(creationPaymentResponse);

        when(logActivityService.buildCommonData("00000018592884", "0.0.0.0", response)).thenReturn(buildSuccessStatus("00000018592884", "0.0.0.0"));

        // When
        switchActivityLogService.enterEnterPinIsCorrect("1234567890", "00000018592884", "0.0.0.0",
                requestBody, response);

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_enter_pin_is_correct_given_correlation_id_and_crm_id_and_status_and_ip_address_and_creation_request_body_and_creation_response_null() {
        // Given
        OrderCreationPaymentRequestBody paymentRequestBody = new OrderCreationPaymentRequestBody();
        paymentRequestBody.setFullRedemption("U");
        paymentRequestBody.setRedeemType("A");
        paymentRequestBody.setOrderAmount("10");

        TmbOneServiceResponse<OrderCreationPaymentResponse> response = new TmbOneServiceResponse<>();

        when(logActivityService.buildCommonData("00000018592884", "0.0.0.0", response)).thenReturn(buildFailedStatus("00000018592884", "0.0.0.0"));

        // When
        switchActivityLogService.enterEnterPinIsCorrect("1234567890", "00000018592884", "0.0.0.0",
                paymentRequestBody, response);

        // Then
        verify(logActivityService).createLog(any());
    }
}