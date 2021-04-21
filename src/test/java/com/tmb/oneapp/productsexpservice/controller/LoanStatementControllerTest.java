package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCreditCardDetailsReq;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeStatus;
import com.tmb.oneapp.productsexpservice.model.loan.*;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.CardStatement;
import com.tmb.oneapp.productsexpservice.model.response.buildstatement.BilledStatementResponse;
import feign.FeignException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertEquals;
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
    public void testGetLoanAccountDetail() {
        String correlationId = "c83936c284cb398fA46CF16F399C";
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
        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> result = loanStatementController.getLoanStatement(correlationId, requestBody);
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    public void testGetLoanAccountDetailTest() throws TMBCommonException {
        String correlationId = "c83936c284cb398fA46CF16F399C";
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
        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> result = loanStatementController.getLoanStatement(correlationId, requestBody);

        assertEquals(200, result.getStatusCodeValue());

    }

    @Test
    public void testHandlingFailedResponse() {
        TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        BilledStatementResponse setCreditLimitResp = new BilledStatementResponse();
        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        silverlakeStatus.setStatusCode(1);
        setCreditLimitResp.setStatus(silverlakeStatus);
        setCreditLimitResp.setTotalRecords(10);
        setCreditLimitResp.setMaxRecords(100);
        setCreditLimitResp.setMoreRecords("100");
        setCreditLimitResp.setSearchKeys("N");
        CardStatement cardStatement = new CardStatement();
        cardStatement.setPromotionFlag("Y");
        setCreditLimitResp.setCardStatement(cardStatement);
        oneServiceResponse.setData(setCreditLimitResp);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_CORRELATION_ID, "123");
        when(accountRequestClient.getLoanAccountStatement(any(), any())).thenThrow(new
                IllegalStateException("Error occurred"));
        final TmbOneServiceResponse<LoanStatementResponse> loanStatementResponse = new TmbOneServiceResponse();
        TmbStatus status = new TmbStatus();
        status.setCode("0");
        status.setDescription("Success");
        status.setMessage("Success");
        status.setService("loan-statement-service");
        loanStatementResponse.setStatus(status);
        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> result = loanStatementController.getTmbOneServiceResponseResponseEntity(responseHeaders, loanStatementResponse);

        Assert.assertNotEquals("0001", result.getBody().getStatus().getCode());
    }

    @Test
    void testGetLoanDetailsNull() {
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FetchCreditCardDetailsReq req = new FetchCreditCardDetailsReq();
        Account accountId = new Account();
        accountId.setBranchId("001");
        accountId.setProductId("ABHA");
        AccountId accountNo = new AccountId();
        accountNo.setAccountNo("00016109738001");
        req.setAccountId("0000000050078360018000167");
        LoanStatementRequest requestBody = new LoanStatementRequest();
        requestBody.setAccountId("00015719933001");
        requestBody.setEndDate("2021-03-25");
        requestBody.setStartDate("2020-03-01");
        when(accountRequestClient.getLoanAccountStatement(any(), any())).thenThrow(FeignException.class);
        Assertions.assertDoesNotThrow(() -> loanStatementController.getLoanStatement(correlationId, requestBody));

    }

    @Test
    void getEntity() {
        LoanStatementRequest requestBody = new LoanStatementRequest();
        requestBody.setAccountId("00015719933001");
        requestBody.setEndDate("2021-03-25");
        requestBody.setStartDate("2020-03-01");
        when(accountRequestClient.getLoanAccountStatement(any(), any())).thenThrow(FeignException.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("test");
        TmbOneServiceResponse<LoanStatementResponse> serviceResponse = new TmbOneServiceResponse<>();
        LoanStatementResponse data = new LoanStatementResponse();
        Status status = new Status();
        status.setAccountStatus("Test");
        status.setContractDate("test");
        data.setStatus(status);
        serviceResponse.setData(data);
        Exception exception = new Exception("FeignClientException");
        StackTraceElement[] stack = {};
        exception.setStackTrace(stack);
        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> entity = loanStatementController.failedErrorResponse(headers, serviceResponse, exception);
        assertEquals(400, entity.getStatusCodeValue());
    }
}
