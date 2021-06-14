package com.tmb.oneapp.productsexpservice.controller;


import com.tmb.oneapp.productsexpservice.model.loan.LoanSubmissionResponse;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmissionRequest;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionCustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    public void testGetFacilitySuccess() throws ServiceException, RemoteException{
        LoanSubmissionRequest request = new LoanSubmissionRequest();
        request.setCaID(2021053104186868L);
        LoanSubmissionResponse response = new LoanSubmissionResponse();
        when(loanSubmissionCustomerService.getCustomerInfo(request.getCaID())).thenReturn(response);
        assertNotNull(response);

    }
}