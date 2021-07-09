package com.tmb.oneapp.productsexpservice.activitylog.portfolio.service;

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
}