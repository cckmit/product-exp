package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.legacy.rsl.ws.incomemodel.response.ResponseIncomeModel;
import com.tmb.common.model.legacy.rsl.ws.instant.application.create.response.Body;
import com.tmb.common.model.legacy.rsl.ws.instant.application.create.response.ResponseInstantLoanCreateApplication;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionCreateApplicationClient;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmitRegisterRequest;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanSubmissionCreateApplicationServiceTest {

    @Mock
    private LoanSubmissionCreateApplicationClient loanSubmissionCreateApplicationClient;

    LoanSubmissionCreateApplicationService loanSubmissionCreateApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanSubmissionCreateApplicationService = new LoanSubmissionCreateApplicationService(loanSubmissionCreateApplicationClient);
    }

    @Test
    public void submitRegisterApplicationAppTypeIsCC() throws ServiceException, RemoteException {
        ResponseInstantLoanCreateApplication clientRes = new ResponseInstantLoanCreateApplication();
        Body body = new Body();
        body.setAppType("CC");
        clientRes.setBody(body);
        LoanSubmitRegisterRequest req = new LoanSubmitRegisterRequest();
        req.setAppType("CC");
        when(loanSubmissionCreateApplicationClient.submitRegister(any(), any())).thenReturn(clientRes);
        ResponseInstantLoanCreateApplication result = loanSubmissionCreateApplicationService.submitRegisterApplication(req, "transType");
        assertEquals("CC", result.getBody().getAppType());
    }

    @Test
    public void submitRegisterApplicationAppTypeNoneCC() throws ServiceException, RemoteException {
        ResponseInstantLoanCreateApplication clientRes = new ResponseInstantLoanCreateApplication();
        Body body = new Body();
        body.setAppType("C2G");
        clientRes.setBody(body);
        LoanSubmitRegisterRequest req = new LoanSubmitRegisterRequest();
        req.setAppType("C2G");
        when(loanSubmissionCreateApplicationClient.submitRegister(any(), any())).thenReturn(clientRes);
        ResponseInstantLoanCreateApplication result = loanSubmissionCreateApplicationService.submitRegisterApplication(req, "transType");
        assertEquals("C2G", result.getBody().getAppType());
    }
}