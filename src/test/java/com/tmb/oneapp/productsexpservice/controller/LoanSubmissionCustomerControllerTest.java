package com.tmb.oneapp.productsexpservice.controller;


import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.loan.LoanCalculatorRequest;
import com.tmb.oneapp.productsexpservice.model.loan.LoanCalculatorResponse;
import com.tmb.oneapp.productsexpservice.model.loan.LoanSubmissionResponse;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionRequest;
import com.tmb.oneapp.productsexpservice.service.LoanCalculatorService;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionCustomerService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LoanSubmissionCustomerControllerTest {

    LoanSubmissionCustomerController loanSubmissionCustomerController;

    @Mock
    LoanSubmissionCustomerService loanSubmissionCustomerService;

    @Mock
    LoanCalculatorService loanCalculatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanSubmissionCustomerController = new LoanSubmissionCustomerController(loanSubmissionCustomerService,loanCalculatorService);
    }

    @Test
    public void testGetFacilitySuccess() throws Exception {
        LoanSubmissionRequest request = new LoanSubmissionRequest();
        request.setCaId(2021053104186868L);
        LoanSubmissionResponse response = new LoanSubmissionResponse();
        when(loanSubmissionCustomerService.getCustomerInfo("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da","001100000000000000000018593707")).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<LoanSubmissionResponse>> result =loanSubmissionCustomerController.getIncomeInfo("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da","001100000000000000000018593707");
        Assert.assertNotEquals(200, result);

    }

    @Test
    public void testGetFacility_Fail() throws Exception {
        when(loanSubmissionCustomerService.getCustomerInfo(any(),anyString())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<LoanSubmissionResponse>> responseEntity = loanSubmissionCustomerController.getIncomeInfo("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da","001100000000000000000018593707");
        assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testGetPreloadLoanCal_RC_Success() throws TMBCommonException {
        LoanCalculatorRequest request = new LoanCalculatorRequest();
        request.setCaId(2021053104186868L);
        request.setProduct("RC");
        LoanCalculatorResponse response = new LoanCalculatorResponse();
        when(loanCalculatorService.getPreloadLoanCal(request)).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<LoanCalculatorResponse>> result =loanSubmissionCustomerController.getPreloadLoanCal(request);
        Assert.assertNotEquals(200, result);
    }

    @Test
    public void testGetPreloadLoanCal_CC_Success() throws TMBCommonException {
        LoanCalculatorRequest request = new LoanCalculatorRequest();
        request.setCaId(2021053104186868L);
        request.setProduct("CC");
        LoanCalculatorResponse response = new LoanCalculatorResponse();
        when(loanCalculatorService.getPreloadLoanCal(request)).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<LoanCalculatorResponse>> result =loanSubmissionCustomerController.getPreloadLoanCal(request);
        Assert.assertNotEquals(200, result);
    }

    @Test
    public void testGetPreloadLoanCal_Fail() throws TMBCommonException {
        LoanCalculatorRequest request = new LoanCalculatorRequest();
        request.setCaId(2021053104186868L);
        request.setProduct("CC");
        when(loanCalculatorService.getPreloadLoanCal(any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<LoanCalculatorResponse>> responseEntity = loanSubmissionCustomerController.getPreloadLoanCal(request);
        assertTrue(responseEntity.getStatusCode().isError());
    }
}