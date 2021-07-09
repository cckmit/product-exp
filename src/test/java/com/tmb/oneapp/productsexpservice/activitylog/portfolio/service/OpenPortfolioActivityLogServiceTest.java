package com.tmb.oneapp.productsexpservice.activitylog.portfolio.service;

import com.tmb.oneapp.productsexpservice.activitylog.portfolio.request.OpenPortfolioActivityLogRequest;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OpenPortfolioActivityLogServiceTest {

    @Mock
    private LogActivityService logActivityService;

    @InjectMocks
    private OpenPortfolioActivityLogService openPortfolioActivityLogService;

    @Test
    void should_call_create_log_when_call_open_portfolio_given_correlation_id_and_crm_id_and_initial_portfolio_and_reason_value() {
        // Given
        // When
        openPortfolioActivityLogService.openPortfolio("1234567890", "00000018592884", "Yes", "");

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_accept_term_and_condition_given_correlation_id_and_crm_id_and_initial_portfolio_and_value() {
        // Given
        // When
        openPortfolioActivityLogService.acceptTermAndCondition("1234567890", "00000018592884", "Yes");

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_click_confirm_given_correlation_id_and_crm_id_and_open_portfolio_activity_log_request() {
        // Given
        // When
        OpenPortfolioActivityLogRequest openPortfolioActivityLogRequest = OpenPortfolioActivityLogRequest.builder()
                .scoreValue("")
                .nickname("")
                .purposeOfInvestment("")
                .receivingAccount("")
                .address("")
                .build();
        openPortfolioActivityLogService.clickConfirm("1234567890", "00000018592884", openPortfolioActivityLogRequest);

        // Then
        verify(logActivityService).createLog(any());
    }

    @Test
    void should_call_create_log_when_call_enter_correct_pin_given_correlation_id_and_crm_id_and_status_and_portfolio_number_and_portfolio_nickname() {
        // Given
        // When
        openPortfolioActivityLogService.enterCorrectPin("1234567890", "00000018592884", "Success", "PT12345", "Buy house");

        // Then
        verify(logActivityService).createLog(any());
    }
}