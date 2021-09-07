package com.tmb.oneapp.productsexpservice.activitylog.buy.service;

import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
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
                .processFlag("Y")
                .unitHolderNumber("unit holder number")
                .fundThaiClassName("thai")
                .build();

        // When
        buyActivityLogService.ClickPurchaseButtonAtFundFactSheetScreen("1234567890", "00000018592884", alternativeBuyRequest, "reason");

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_click_purchase_button_at_fund_fact_sheet_screen_given_correlation_id_and_crm_id_and_alternative_buy_request_fail_and_reason() {
        // Given
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder()
                .processFlag("N")
                .fundEnglishClassName("english")
                .build();

        // When
        buyActivityLogService.ClickPurchaseButtonAtFundFactSheetScreen("1234567890", "00000018592884", alternativeBuyRequest, "reason");

        // Then
        verify(logActivityService).createLog(any());
    }
}