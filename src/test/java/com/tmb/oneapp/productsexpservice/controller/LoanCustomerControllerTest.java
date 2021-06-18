package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerSubmissionRequest;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerResponse;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerSubmissionResponse;
import com.tmb.oneapp.productsexpservice.service.LoanCustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LoanCustomerControllerTest {

    @Mock
    LoanCustomerService loanCustomerService;
    @InjectMocks
    LoanCustomerController loanCustomerController;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loanCustomerController = new LoanCustomerController(loanCustomerService);
    }

    @Test
    public void testGetLoanCustomerProfileSuccess() throws Exception {
        LoanCustomerRequest request = new LoanCustomerRequest();
        request.setCaID(1L);
        String correlationId = "xxx";
        LoanCustomerResponse response = new LoanCustomerResponse();
        when(loanCustomerService.getCustomerProfile(any(),any(), any())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<LoanCustomerResponse>> responseEntity = loanCustomerController.getLoanCustomerProfile("11",correlationId, request);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetLoanCustomerProfileFail() throws Exception {
        LoanCustomerRequest request = new LoanCustomerRequest();
        request.setCaID(1L);
        String correlationId = "xxx";
        when(loanCustomerService.getCustomerProfile(any(),any(), any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<LoanCustomerResponse>> responseEntity = loanCustomerController.getLoanCustomerProfile("11",correlationId, request);
        assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testSaveCustomerProfileSuccess() throws Exception {
        LoanCustomerSubmissionRequest request = new LoanCustomerSubmissionRequest();
        request.setCaID(1L);
        request.setFeatureType("S");
        request.setRequestAmount(BigDecimal.valueOf(30000));
        request.setTenure(3L);
        request.setDisburstAccountNo("xxx");
        request.setDisburstAccountName("abc");
        String correlationId = "xxx";
        LoanCustomerSubmissionResponse response = new LoanCustomerSubmissionResponse();
        when(loanCustomerService.saveCustomerSubmission(any())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<LoanCustomerSubmissionResponse>> responseEntity = loanCustomerController.saveCustomerProfile(correlationId, request);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testSaveCustomerProfileFail() throws Exception {
        LoanCustomerSubmissionRequest request = new LoanCustomerSubmissionRequest();
        request.setCaID(1L);
        request.setFeatureType("S");
        request.setRequestAmount(BigDecimal.valueOf(30000));
        request.setTenure(3L);
        request.setDisburstAccountNo("xxx");
        request.setDisburstAccountName("abc");
        String correlationId = "xxx";
        when(loanCustomerService.saveCustomerSubmission(any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<LoanCustomerSubmissionResponse>> responseEntity = loanCustomerController.saveCustomerProfile(correlationId, request);
        assertTrue(responseEntity.getStatusCode().isError());
    }

}
