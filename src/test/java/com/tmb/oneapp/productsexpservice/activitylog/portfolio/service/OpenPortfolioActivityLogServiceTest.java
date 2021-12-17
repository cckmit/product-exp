package com.tmb.oneapp.productsexpservice.activitylog.portfolio.service;

import com.tmb.oneapp.productsexpservice.activitylog.portfolio.request.OpenPortfolioActivityLogRequest;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.tmb.oneapp.productsexpservice.activitylog.util.ActivityStatusUtil.buildSuccessStatus;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenPortfolioActivityLogServiceTest {

    @Mock
    private LogActivityService logActivityService;

    @InjectMocks
    private OpenPortfolioActivityLogService openPortfolioActivityLogService;

    @Test
    void should_call_create_log_when_call_open_portfolio_given_correlation_id_and_crm_id_and_ip_address_and_initial_portfolio_and_reason_value() {
        // Given
        when(logActivityService.buildCommonData("00000018592884", "0.0.0.0")).thenReturn(buildSuccessStatus("00000018592884", "0.0.0.0"));

        // When
        openPortfolioActivityLogService.openPortfolio("1234567890", "00000018592884", "0.0.0.0", "Yes", "");

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_accept_term_and_condition_given_correlation_id_and_crm_id_and_ip_address_and_initial_portfolio() {
        // Given
        when(logActivityService.buildCommonData("00000018592884", "0.0.0.0")).thenReturn(buildSuccessStatus("00000018592884", "0.0.0.0"));

        // When
        openPortfolioActivityLogService.acceptTermAndCondition("1234567890", "00000018592884", "0.0.0.0");

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_click_confirm_given_correlation_id_and_crm_id_and_ip_address_and_open_portfolio_activity_log_request() {
        // Given
        when(logActivityService.buildCommonData("00000018592884", "0.0.0.0")).thenReturn(buildSuccessStatus("00000018592884", "0.0.0.0"));

        // When
        OpenPortfolioActivityLogRequest openPortfolioActivityLogRequest = OpenPortfolioActivityLogRequest.builder()
                .scoreValue("")
                .nickname("")
                .purposeOfInvestment("")
                .receivingAccount("")
                .address("")
                .build();
        openPortfolioActivityLogService.clickConfirm("1234567890", "00000018592884", "0.0.0.0", openPortfolioActivityLogRequest);

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_enter_correct_pin_given_correlation_id_and_crm_id_and_ip_address_and_status_and_portfolio_number_and_portfolio_nickname() {
        // Given
        when(logActivityService.buildCommonData("00000018592884", "0.0.0.0")).thenReturn(buildSuccessStatus("00000018592884", "0.0.0.0"));

        // When
        openPortfolioActivityLogService.enterPinIsCorrect("1234567890", "00000018592884", "0.0.0.0", "Success", "PT12345", "Buy house");

        // Then
        verify(logActivityService).createLog(any());
    }
}