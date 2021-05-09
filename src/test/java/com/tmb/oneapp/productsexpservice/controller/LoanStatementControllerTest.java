package com.tmb.oneapp.productsexpservice.controller;

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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    public void testGetLoanStatement() {
        when(accountRequestClient.getLoanAccountStatement(anyString(), any())).thenReturn(null);

        LoanStatementRequest requestBody = getLoanStatementRequest();

        TmbOneServiceResponse<LoanStatementResponse> resp = getLoanStatementResponseTmbOneServiceResponse();

        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> response = new ResponseEntity<>(resp, HttpStatus.OK);

        when(accountRequestClient.getLoanAccountStatement(anyString(), any())).thenReturn(response);

        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> result = loanStatementController.getLoanStatement("correlationId", requestBody);
        Assert.assertNotEquals(200, result);
    }

    @Test
    public void testGetLoanAccountDetailTest() {
        String correlationId = "c83936c284cb398fA46CF16F399C";
        LoanStatementRequest requestBody = getLoanStatementRequest();
        final TmbOneServiceResponse<LoanStatementResponse> loanStatementResponseTmbOneServiceResponse = getLoanStatementResponseTmbOneServiceResponse();
        final ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> tmbOneServiceResponseEntity = new ResponseEntity<>(loanStatementResponseTmbOneServiceResponse, HttpStatus.OK);
        when(accountRequestClient.getLoanAccountStatement(anyString(), any())).thenReturn(tmbOneServiceResponseEntity);
        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> result = loanStatementController.getLoanStatement(correlationId, requestBody);

        assertNotEquals(200, result.getStatusCodeValue());

    }

    private TmbOneServiceResponse<LoanStatementResponse> getLoanStatementResponseTmbOneServiceResponse() {
        TmbOneServiceResponse<LoanStatementResponse> loanStatementResponseTmbOneServiceResponse = new TmbOneServiceResponse<>();
        loanStatementResponseTmbOneServiceResponse.setStatus(new TmbStatus("code", "message", "service", "description"));
        LoanStatementResponse loanStatementResponse = new LoanStatementResponse();
        Status status = new Status();
        status.setAccountStatus("accountStatus");
        status.setContractDate("contractDate");
        loanStatementResponse.setStatus(status);
        AdditionalStatus additionalStatus = new AdditionalStatus();
        additionalStatus.setStatusCode("statusCode");
        additionalStatus.setServerStatusCode("serverStatusCode");
        additionalStatus.setSeverity("severity");
        additionalStatus.setStatusDesc("statusDesc");
        loanStatementResponse.setAdditionalStatus(List.of(additionalStatus));
        AccountResponse response = new AccountResponse();
        response.setId("id");
        Statement statement = new Statement();
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
        return loanStatementResponseTmbOneServiceResponse;
    }

    private LoanStatementRequest getLoanStatementRequest() {
        LoanStatementRequest requestBody = new LoanStatementRequest();
        requestBody.setAccountId("00015719933001");
        requestBody.setEndDate("2021-03-25");
        requestBody.setStartDate("2020-03-01");
        return requestBody;
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
        LoanStatementRequest requestBody = getLoanStatementRequest();
        when(accountRequestClient.getLoanAccountStatement(any(), any())).thenThrow(FeignException.class);
        Assertions.assertDoesNotThrow(() -> loanStatementController.getLoanStatement(correlationId, requestBody));

    }

    @Test
    void getEntity() {
        getLoanStatementRequest();
        when(accountRequestClient.getLoanAccountStatement(any(), any())).thenThrow(FeignException.class);
        HttpHeaders headers = getHttpHeaders();
        TmbOneServiceResponse<LoanStatementResponse> serviceResponse = getTmbOneServiceResponse();
        Exception exception = new Exception("FeignClientException");
        StackTraceElement[] stack = {};
        exception.setStackTrace(stack);
        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> entity = loanStatementController.failedErrorResponse(headers, serviceResponse, exception);
        assertEquals(400, entity.getStatusCodeValue());
    }

    private TmbOneServiceResponse<LoanStatementResponse> getTmbOneServiceResponse() {
        TmbOneServiceResponse<LoanStatementResponse> serviceResponse = new TmbOneServiceResponse<>();
        LoanStatementResponse data = new LoanStatementResponse();
        Status status = new Status();
        status.setAccountStatus("Test");
        status.setContractDate("test");
        data.setStatus(status);
        serviceResponse.setData(data);
        return serviceResponse;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("test");
        return headers;
    }

    @Test
    public void testGetTmbOneServiceResponseResponseEntity() {
        HttpHeaders responseHeaders = getHttpHeaders();
        TmbOneServiceResponse<LoanStatementResponse> serviceResponse = getTmbOneServiceResponse();
        ResponseEntity<TmbOneServiceResponse<LoanStatementResponse>> result = loanStatementController.getTmbOneServiceResponseResponseEntity(responseHeaders, serviceResponse);
        Assert.assertNotEquals(null, result);
    }
}
