package com.tmb.oneapp.productsexpservice.activitylog.portfolio.controller;

import com.tmb.oneapp.productsexpservice.activitylog.portfolio.request.OpenPortfolioActivityLogRequest;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.service.OpenPortfolioActivityLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OpenPortfolioActivityControllerTest {

    @Mock
    private OpenPortfolioActivityLogService openPortfolioActivityLogService;

    @InjectMocks
    private OpenPortfolioActivityController openPortfolioActivityController;

    @Test
    void should_call_click_confirm_when_call_click_confirm_given_header_and_body() {
        // Given
        OpenPortfolioActivityLogRequest openPortfolioActivityLogRequest = OpenPortfolioActivityLogRequest.builder()
                .scoreValue("")
                .nickname("")
                .purposeOfInvestment("")
                .receivingAccount("")
                .address("")
                .build();
        // When

        openPortfolioActivityController.clickConfirm("1234567890", "00000018592884", "0.0.0.0", openPortfolioActivityLogRequest);

        // Then
        verify(openPortfolioActivityLogService).clickConfirm("1234567890", "00000018592884", "0.0.0.0", openPortfolioActivityLogRequest);
    }
}
