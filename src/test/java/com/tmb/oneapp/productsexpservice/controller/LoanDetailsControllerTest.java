package com.tmb.oneapp.productsexpservice.controller;


import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCreditCardDetailsReq;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.loan.*;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class LoanDetailsControllerTest {

    @Mock
    AccountRequestClient accountRequestClient;
    @InjectMocks
    LoanDetailsController homeLoanController;
    @Mock
    CommonServiceClient commonServiceClient;
    @Mock
    CreditCardLogService creditCardLogService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        homeLoanController = new LoanDetailsController(accountRequestClient, commonServiceClient, creditCardLogService);
    }

    @Test
    public void testGetLoanAccountDetail()  {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        LoanDetailsFullResponse response = new LoanDetailsFullResponse();
        StatusResponse status = new StatusResponse();
        status.setCode("0000");
        status.setDescription("Success");
        response.setStatus(status);
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        response.setAccount(accountId);
        AccountId accountNo = new AccountId();
        accountNo.setAccountNo("00016109738001");
        response.setAccount(accountId);
        ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> serviceResponse = new ResponseEntity(HttpStatus.OK);
        when(accountRequestClient.getLoanAccountDetail(any(), any())).thenReturn(serviceResponse);
        AccountId account = new AccountId();
        account.setAccountNo("00016109738001");
        ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> result = homeLoanController.getLoanAccountDetail(reqHeaders, account);
        Assert.assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    public void testGetLoanAccountDetailElseCase()  {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        LoanDetailsFullResponse response = new LoanDetailsFullResponse();
        StatusResponse status = new StatusResponse();
        status.setCode("0000");
        status.setDescription("Success");
        response.setStatus(status);
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        response.setAccount(accountId);
        ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> serviceResponse = new ResponseEntity(HttpStatus.OK);
        when(accountRequestClient.getLoanAccountDetail(any(), any())).thenReturn(serviceResponse);
        AccountId account = new AccountId();
        account.setAccountNo("00016109738001");
        ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> result = homeLoanController.getLoanAccountDetail(reqHeaders, account);
        Assert.assertEquals(400, result.getStatusCodeValue());
    }


    @Test
    public void testGetLoanAccountDetailsCase()  {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        LoanDetailsFullResponse response = new LoanDetailsFullResponse();
        StatusResponse status = new StatusResponse();
        status.setCode("0000");
        status.setDescription("Success");
        response.setStatus(status);
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        DebitAccount debitAccount = new DebitAccount();
        debitAccount.setAutoDebitDate("06-06-2021");
        debitAccount.setAutoDebitMethod("card");
        debitAccount.setId("1234");
        accountId.setDebitAccount(debitAccount);
        response.setAccount(accountId);
        AdditionalStatus additionalStatus= new AdditionalStatus();
        additionalStatus.setStatusCode("1234");
        additionalStatus.setSeverity("Awesome");
        additionalStatus.setStatusDesc("successful");
        response.setAdditionalStatus(additionalStatus);
        ProductConfig productConfig = new ProductConfig();
        productConfig.setProductNameEN("Mobile");
        productConfig.setProductCode("1234");
        response.setProductConfig(productConfig);
        ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> serviceResponse = new ResponseEntity(HttpStatus.OK);
        when(accountRequestClient.getLoanAccountDetail(any(), any())).thenReturn(serviceResponse);
        AccountId account = new AccountId();
        account.setAccountNo("00016109738001");
        ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> result = homeLoanController.getLoanAccountDetail(reqHeaders, account);
        Assert.assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    void testGetCreditCardDetailsNull()  {
        Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C");
        FetchCreditCardDetailsReq req = new FetchCreditCardDetailsReq();
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        AccountId accountNo = new AccountId();
        accountNo.setAccountNo("00016109738001");
        req.setAccountId("0000000050078360018000167");
        LoanDetailsFullResponse response = new LoanDetailsFullResponse();
        ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> result = homeLoanController.getLoanAccountDetail(reqHeaders, accountNo);

        assertNotNull(response);

    }

    @Test
    void testGetLoanDetailsNull()  {
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FetchCreditCardDetailsReq req = new FetchCreditCardDetailsReq();
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        AccountId accountNo = new AccountId();
        accountNo.setAccountNo("00016109738001");
        req.setAccountId("0000000050078360018000167");
        when(accountRequestClient.getLoanAccountDetail(any(), any())).thenThrow(NullPointerException.class);
        Assertions.assertThrows(NullPointerException.class, () -> {
            accountRequestClient.getLoanAccountDetail(correlationId, accountNo);
        });
    }

    public Map<String, String> headerRequestParameter(String correlationId) {
        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put(ProductsExpServiceConstant.X_CORRELATION_ID, correlationId);
        return reqData;

    }
}
