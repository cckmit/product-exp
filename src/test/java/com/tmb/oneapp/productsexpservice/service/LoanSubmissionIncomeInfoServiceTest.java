package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.legacy.rsl.ws.incomemodel.response.Body;
import com.tmb.common.model.legacy.rsl.ws.incomemodel.response.ResponseIncomeModel;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetIncomeModelInfoClient;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class LoanSubmissionIncomeInfoServiceTest {

    @Mock
    private LoanSubmissionGetIncomeModelInfoClient loanSubmissionGetIncomeModelInfoClient;

    LoanSubmissionIncomeInfoService loanSubmissionIncomeInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanSubmissionIncomeInfoService = new LoanSubmissionIncomeInfoService(loanSubmissionGetIncomeModelInfoClient);
    }

    @Test
    public void testGetIncomeInfoByRmId() throws ServiceException, RemoteException {
        ResponseIncomeModel clientRes = new ResponseIncomeModel();
        Body body = new Body();
        body.setIncomeModelAmt(BigDecimal.valueOf(100));
        clientRes.setBody(body);
        when(loanSubmissionGetIncomeModelInfoClient.getIncomeInfo(any())).thenReturn(clientRes);
        IncomeInfo result = loanSubmissionIncomeInfoService.getIncomeInfoByRmId("rmId");
        assertEquals(BigDecimal.valueOf(100),result.getIncomeAmount());
    }
}