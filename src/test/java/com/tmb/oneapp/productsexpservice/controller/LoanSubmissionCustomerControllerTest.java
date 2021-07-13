package com.tmb.oneapp.productsexpservice.controller;


import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.loan.LoanSubmissionResponse;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionRequest;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionCustomerService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LoanSubmissionCustomerControllerTest {

    LoanSubmissionCustomerController loanSubmissionCustomerController;

    @Mock
    LoanSubmissionCustomerService loanSubmissionCustomerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanSubmissionCustomerController = new LoanSubmissionCustomerController(loanSubmissionCustomerService);
    }

    @Test
    public void testGetFacilitySuccess() throws ServiceException, RemoteException {
        LoanSubmissionRequest request = new LoanSubmissionRequest();
        request.setCaId(2021053104186868L);
        LoanSubmissionResponse response = new LoanSubmissionResponse();
        when(loanSubmissionCustomerService.getCustomerInfo(request.getCaId())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<LoanSubmissionResponse>> result =loanSubmissionCustomerController.getIncomeInfo(request);
        Assert.assertNotEquals(200, result);

    }
}