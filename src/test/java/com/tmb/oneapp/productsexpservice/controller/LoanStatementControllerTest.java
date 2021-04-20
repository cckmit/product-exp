package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.model.loan.*;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LoanStatementControllerTest {


    @Mock
    TMBLogger<LoanStatementController> log;
    @Mock
    AccountRequestClient accountRequestClient;
    @InjectMocks
    LoanStatementController loanStatementController;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loanStatementController = new LoanStatementController(accountRequestClient);
    }

    @Test
    public void testGetLoanAccountDetail()  {
        String correlationId="c83936c284cb398fA46CF16F399C";
        TmbOneServiceResponse<LoanStatementResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus status = new TmbStatus();
        status.setDescription("working");
        status.setCode("0");
        status.setMessage("Successful");
        status.setService("loan-statement-get");
        oneServiceResponse.setStatus(status);
        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> response = new ResponseEntity<>(oneServiceResponse, HttpStatus.OK);
        when(accountRequestClient.getLoanAccountStatement(anyString(), any())).thenReturn(response);

        LoanStatementRequest requestBody = new LoanStatementRequest();
        requestBody.setAccountId("00015719933001");
        requestBody.setEndDate("2021-03-25");
        requestBody.setStartDate("2020-03-01");
        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> result = loanStatementController.getLoanAccountDetail(correlationId, requestBody);
        Assert.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    public void testGetLoanAccountDetailTest() throws Exception {
        // Setup

        // Configure AccountRequestClient.getLoanAccountStatement(...).
        String correlationId="c83936c284cb398fA46CF16F399C";
        LoanStatementRequest requestBody = new LoanStatementRequest();
        requestBody.setAccountId("00015719933001");
        requestBody.setEndDate("2021-03-25");
        requestBody.setStartDate("2020-03-01");
        final TmbOneServiceResponse<LoanStatementResponse> loanStatementResponseTmbOneServiceResponse = new TmbOneServiceResponse<>();
        loanStatementResponseTmbOneServiceResponse.setStatus(new TmbStatus("code", "message", "service", "description"));
        final LoanStatementResponse loanStatementResponse = new LoanStatementResponse();
        final Status status = new Status();
        status.setAccountStatus("accountStatus");
        status.setContractDate("contractDate");
        loanStatementResponse.setStatus(status);
        final AdditionalStatus additionalStatus = new AdditionalStatus();
        additionalStatus.setStatusCode("statusCode");
        additionalStatus.setServerStatusCode("serverStatusCode");
        additionalStatus.setSeverity("severity");
        additionalStatus.setStatusDesc("statusDesc");
        loanStatementResponse.setAdditionalStatus(List.of(additionalStatus));
        final AccountResponse response = new AccountResponse();
        response.setId("id");
        final Statement statement = new Statement();
        statement.setSequenceNo("sequenceNo");
        statement.setTransactionDate("transactionDate");
        statement.setTransactionCode("transactionCode");
        statement.setLoanBalanceAmount("loanBalanceAmount");
        statement.setInterestAmount("interestAmount");
        statement.setTransactionAmount("transactionAmount");
        statement.setInterestRate("interestRate");
        statement.setOutstandingBalance("outstandingBalance");
        statement.setOutstandingInterest("outstandingInterest");
        statement.setFeeAmount("feeAmount");
        response.setStatements(List.of(statement));
        loanStatementResponse.setResponse(response);
        loanStatementResponseTmbOneServiceResponse.setData(loanStatementResponse);
        final ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> tmbOneServiceResponseEntity = new ResponseEntity<>(loanStatementResponseTmbOneServiceResponse, HttpStatus.OK);
        when(accountRequestClient.getLoanAccountStatement(anyString(), any())).thenReturn(tmbOneServiceResponseEntity);
        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> result = loanStatementController.getLoanAccountDetail(correlationId, requestBody);

        // Run the test

        // Verify the results
        Assert.assertEquals(200, result.getStatusCodeValue());

    }
}
