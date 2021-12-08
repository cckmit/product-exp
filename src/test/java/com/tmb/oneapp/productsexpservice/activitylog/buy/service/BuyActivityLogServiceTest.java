package com.tmb.oneapp.productsexpservice.activitylog.buy.service;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Account;
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
class BuyActivityLogServiceTest {

    @Mock
    private LogActivityService logActivityService;

    @InjectMocks
    private BuyActivityLogService buyActivityLogService;

    @Test
    void should_call_create_log_when_call_click_purchase_button_at_fund_fact_sheet_screen_given_correlation_id_and_crm_id_and_alternative_buy_request_success_and_reason() {
        // Given
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder()
                .unitHolderNumber("unit holder number")
                .processFlag("Y")
                .build();

        // When
        buyActivityLogService.clickPurchaseButtonAtFundFactSheetScreen("1234567890", "00000018592884", alternativeBuyRequest, "reason");

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_click_purchase_button_at_fund_fact_sheet_screen_given_correlation_id_and_crm_id_and_alternative_buy_request_fail_and_reason() {
        // Given
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder()
                .fundEnglishClassName("english")
                .processFlag("N")
                .fundName("name")
                .fundEnglishClassName("english")
                .build();

        // When
        buyActivityLogService.clickPurchaseButtonAtFundFactSheetScreen("1234567890", "00000018592884", alternativeBuyRequest, "reason");

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_enter_pin_is_correct_given_correlation_id_and_crm_id_and_status_and_ip_address__creation_request_body_and_creation_response_not_null() {

        // Given
        Account account = new Account();
        account.setAccountId("accountId");
        OrderCreationPaymentRequestBody requestBody = new OrderCreationPaymentRequestBody();
        requestBody.setPortfolioNumber("PT00001");
        requestBody.setOrderAmount("10");
        requestBody.setFundEnglishClassName("english");
        requestBody.setFromAccount(account);

        OrderCreationPaymentResponse creationPaymentResponse = new OrderCreationPaymentResponse();
        creationPaymentResponse.setOrderId("orderId");
        TmbOneServiceResponse<OrderCreationPaymentResponse> response = new TmbOneServiceResponse<>();
        response.setData(creationPaymentResponse);

        when(logActivityService.buildCommonData("00000018592884", "0.0.0.0", response)).thenReturn(buildSuccessStatus("00000018592884", "0.0.0.0"));

        // When
        buyActivityLogService.enterEnterPinIsCorrect("1234567890", "00000018592884", "0.0.0.0",
                requestBody, response);

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_enter_pin_is_correct_given_correlation_id_and_crm_id_and_status_ip_address_and_creation_request_body_and_creation_response_null() {
        // Given
        Account account = new Account();
        account.setAccountId("accountId");
        OrderCreationPaymentRequestBody requestBody = new OrderCreationPaymentRequestBody();
        requestBody.setOrderAmount("10");
        requestBody.setFundEnglishClassName("english");
        requestBody.setFromAccount(account);

        TmbOneServiceResponse<OrderCreationPaymentResponse> response = new TmbOneServiceResponse<>();

        when(logActivityService.buildCommonData("00000018592884", "0.0.0.0", response)).thenReturn(buildFailedStatus("00000018592884", "0.0.0.0"));

        // When
        buyActivityLogService.enterEnterPinIsCorrect("1234567890", "00000018592884", "0.0.0.0",
                requestBody, response);

        // Then
        verify(logActivityService).createLog(any());
    }
}