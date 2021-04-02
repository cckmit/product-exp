package com.tmb.oneapp.productsexpservice.controller;


import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.homeloan.Account;
import com.tmb.oneapp.productsexpservice.model.homeloan.AccountId;
import com.tmb.oneapp.productsexpservice.model.homeloan.LoanDetailsFullResponse;
import com.tmb.oneapp.productsexpservice.model.homeloan.StatusResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class LoanDetailsControllerTest {

    @Mock
    AccountRequestClient accountRequestClient;
    @InjectMocks
    LoanDetailsController homeLoanController;
    @Mock
    CommonServiceClient commonServiceClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        homeLoanController= new LoanDetailsController(accountRequestClient, commonServiceClient);
    }

    @Test
    public void testGetLoanAccountDetail() throws Exception {
        String correlationId="1234";
        LoanDetailsFullResponse response = new LoanDetailsFullResponse();
        StatusResponse status = new StatusResponse();
        status.setCode("0000");
        status.setDescription("Success");
        response.setStatus(status);
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        response.setAccount(accountId);
        AccountId accountNo= new AccountId();
        accountNo.setAccountNo("00016109738001");
        response.setAccount(accountId);
        ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> serviceResponse= new ResponseEntity(HttpStatus.OK);
        when(accountRequestClient.getLoanAccountDetail(any(),any())).thenReturn(serviceResponse);
        AccountId account = new AccountId();
        account.setAccountNo("00016109738001");
        ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> result = homeLoanController.getLoanAccountDetail(correlationId,account);
        Assert.assertEquals(400, result.getStatusCodeValue());
    }
}

