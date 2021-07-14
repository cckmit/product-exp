package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionIncomeInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.xml.rpc.ServiceException;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanSubmissionOnlineControllerTest {

    LoanSubmissionOnlineController loanSubmissionOnlineController;

    @Mock
    LoanSubmissionIncomeInfoService loanSubmissionIncomeInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanSubmissionOnlineController = new LoanSubmissionOnlineController(loanSubmissionIncomeInfoService);
    }

    @Test
    public void testGetIncomeInfoByRmIdSuccess() throws ServiceException, RemoteException {
        IncomeInfo res = new IncomeInfo();
        res.setIncomeAmount(BigDecimal.valueOf(100));
        when(loanSubmissionIncomeInfoService.getIncomeInfoByRmId(any())).thenReturn(res);
        ResponseEntity<TmbOneServiceResponse<IncomeInfo>> responseEntity = loanSubmissionOnlineController.getIncomeInfo("rmid");
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetIncomeInfoByRmIdFail() throws ServiceException, RemoteException {
        IncomeInfo res = new IncomeInfo();
        res.setIncomeAmount(BigDecimal.valueOf(100));
        when(loanSubmissionIncomeInfoService.getIncomeInfoByRmId(any())).thenThrow(new IllegalArgumentException());
        ResponseEntity<TmbOneServiceResponse<IncomeInfo>> responseEntity = loanSubmissionOnlineController.getIncomeInfo("rmid");
        assertTrue(responseEntity.getStatusCode().isError());
    }

}