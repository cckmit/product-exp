package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionCreateApplicationReq;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionCreateApplicationService;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionIncomeInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanSubmissionOnlineControllerTest {

    LoanSubmissionOnlineController loanSubmissionOnlineController;

    @Mock
    LoanSubmissionIncomeInfoService loanSubmissionIncomeInfoService;

    @Mock
    LoanSubmissionCreateApplicationService loanSubmissionCreateApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanSubmissionOnlineController = new LoanSubmissionOnlineController(loanSubmissionIncomeInfoService, loanSubmissionCreateApplicationService);
    }

    @Test
    public void testGetIncomeInfoByRmIdSuccess() throws  TMBCommonException {
        IncomeInfo res = new IncomeInfo();
        res.setIncomeAmount(BigDecimal.valueOf(100));
        when(loanSubmissionIncomeInfoService.getIncomeInfoByRmId(any())).thenReturn(res);
        ResponseEntity<TmbOneServiceResponse<IncomeInfo>> responseEntity = loanSubmissionOnlineController.getIncomeInfo("rmid");
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetIncomeInfoByRmIdFail() throws TMBCommonException {
        when(loanSubmissionIncomeInfoService.getIncomeInfoByRmId(any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<IncomeInfo>> responseEntity = loanSubmissionOnlineController.getIncomeInfo("rmid");
        assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testCreateApplicationSuccess() throws  TMBCommonException {
        ResponseApplication responseApplication = new ResponseApplication();
        when(loanSubmissionCreateApplicationService.createApplication(any(),any())).thenReturn(responseApplication);
        ResponseEntity<TmbOneServiceResponse<ResponseApplication>> responseEntity = loanSubmissionOnlineController.createApplication("rmid",new LoanSubmissionCreateApplicationReq());
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testCreateApplicationFail() throws  TMBCommonException {
        when(loanSubmissionCreateApplicationService.createApplication(any(),any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<ResponseApplication>> responseEntity = loanSubmissionOnlineController.createApplication("rmid",new LoanSubmissionCreateApplicationReq());
        assertTrue(responseEntity.getStatusCode().isError());
    }

}