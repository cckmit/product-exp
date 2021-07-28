package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class LoanSubmissionIncomeInfoServiceTest {

    @Mock
    private LendingServiceClient lendingServiceClient;

    LoanSubmissionIncomeInfoService loanSubmissionIncomeInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanSubmissionIncomeInfoService = new LoanSubmissionIncomeInfoService(lendingServiceClient);
    }

    @Test
    public void testGetIncomeInfoByRmIdSuccess() throws TMBCommonException {
        IncomeInfo incomeInfo = new IncomeInfo();
        incomeInfo.setIncomeAmount(BigDecimal.valueOf(100));
        incomeInfo.setStatusWorking("salary");
        TmbOneServiceResponse<IncomeInfo> oneServiceResponse = new TmbOneServiceResponse<IncomeInfo>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
        oneServiceResponse.setData(incomeInfo);
        when(lendingServiceClient.getIncomeInfo(any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        IncomeInfo result = loanSubmissionIncomeInfoService.getIncomeInfoByRmId("rmId");
        assertEquals(BigDecimal.valueOf(100), result.getIncomeAmount());
    }

    @Test
    public void testGetIncomeInfoByRmIdFailed() {
        TmbOneServiceResponse<IncomeInfo> oneServiceResponse = new TmbOneServiceResponse<IncomeInfo>();
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "failed", "lending-service"));
        when(lendingServiceClient.getIncomeInfo(any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        assertThrows(Exception.class, () ->
                loanSubmissionIncomeInfoService.getIncomeInfoByRmId("crmid"));
    }
}