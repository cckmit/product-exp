package com.tmb.oneapp.productsexpservice.activitylog.buy.service;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

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
    void should_call_create_log_when_call_enter_pin_is_correct_given_correlation_id_and_crm_id_and_status_and_payment_request_body_and_payment_response_body_not_null() {

        // Given
        Account account = new Account();
        account.setAccountId("accountId");
        OrderCreationPaymentRequestBody paymentRequestBody = new OrderCreationPaymentRequestBody();
        paymentRequestBody.setOrderAmount("10");
        paymentRequestBody.setFundEnglishClassName("english");
        paymentRequestBody.setFromAccount(account);
        OrderCreationPaymentResponse paymentResponseBody = new OrderCreationPaymentResponse();
        paymentResponseBody.setOrderId("orderId");

        // When
        buyActivityLogService.enterEnterPinIsCorrect("1234567890", "00000018592884", "completed",
                paymentRequestBody, paymentResponseBody);

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_enter_pin_is_correct_given_correlation_id_and_crm_id_and_status_and_payment_request_body_and_payment_response_body_null() {
        // Given
        Account account = new Account();
        account.setAccountId("accountId");
        OrderCreationPaymentRequestBody paymentRequestBody = new OrderCreationPaymentRequestBody();
        paymentRequestBody.setOrderAmount("10");
        paymentRequestBody.setFundEnglishClassName("english");
        paymentRequestBody.setFromAccount(account);

        // When
        buyActivityLogService.enterEnterPinIsCorrect("1234567890", "00000018592884", "completed",
                paymentRequestBody, null);

        // Then
        verify(logActivityService).createLog(any());
    }
}