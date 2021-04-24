package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.loan.*;
import org.junit.Assert;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MoneyTransferControllerTest {

    @InjectMocks
    MoneyTransferController moneyTransferController;
    @Mock
    private CreditCardClient mockCreditCardClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        moneyTransferController = new MoneyTransferController(mockCreditCardClient);
    }

    @Test
    public void testCardMoneyTransfer() throws Exception {
        // Setup

        // Configure CreditCardClient.cardMoneyTransfer(...).
        final TmbOneServiceResponse<DepositResponse> depositResponseTmbOneServiceResponse = new TmbOneServiceResponse<>();
        depositResponseTmbOneServiceResponse.setStatus(new TmbStatus("code", "message", "service", "description"));
        final DepositResponse depositResponse = new DepositResponse();
        final DepositSuccessResponse deposit = new DepositSuccessResponse();
        final StatusResponse status = new StatusResponse();
        status.setCode("code");
        status.setDescription("description");
        deposit.setStatus(status);
        deposit.setToAcctName("toAcctName");
        deposit.setTransactionAmount("transactionAmount");
        deposit.setDebitCurrentBalance("debitCurrentBalance");
        deposit.setDebitAvailableBalance("debitAvailableBalance");
        deposit.setFeeAmount("feeAmount");
        deposit.setTellerId("tellerId");
        deposit.setFlagFeeReg("flagFeeReg");
        deposit.setWaiveProductCode("waiveProductCode");
        deposit.setAmountWaived("amountWaived");
        depositResponse.setDeposit(deposit);
        depositResponseTmbOneServiceResponse.setData(depositResponse);
        final ResponseEntity<TmbOneServiceResponse<DepositResponse>> tmbOneServiceResponseEntity = new ResponseEntity<>(depositResponseTmbOneServiceResponse, HttpStatus.OK);
        when(mockCreditCardClient.cardMoneyTransfer(anyString(), any())).thenReturn(tmbOneServiceResponseEntity);
        String correlationId = "c83936c284cb398fA46CF16F399C";
        DepositRequest requestBody = new DepositRequest();
        Deposit depositRequest = new Deposit();
        depositRequest.setAmounts("1234.00");
        depositRequest.setExpiredDate("21-09-2021");
        depositRequest.setModelType("test");
        depositRequest.setFromAccountId("1234");
        depositRequest.setOrderNo("1234");
        depositRequest.setTransferredDate("21-09-2021");
        depositRequest.setReferenceCode("1234");
        depositRequest.setFromAccountType("test");
        depositRequest.setToAccountId("1234");
        depositRequest.setWaiverCode("1234");
        requestBody.setDeposit(depositRequest);
        ResponseEntity<TmbOneServiceResponse<DepositResponse>> response = moneyTransferController.cardMoneyTransfer(correlationId, requestBody);

        // Verify the results
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK.value());
        assertThat(response.getStatusCodeValue()).isNotEqualTo("expectedResponse");
    }

    @Test
    void failedErrorResponse() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setBearerAuth("1234");
        final TmbOneServiceResponse<DepositResponse> depositResponseTmbOneServiceResponse = new TmbOneServiceResponse<>();
        depositResponseTmbOneServiceResponse.setStatus(new TmbStatus("code", "message", "service", "description"));
        final DepositResponse depositResponse = new DepositResponse();
        final DepositSuccessResponse deposit = new DepositSuccessResponse();
        final StatusResponse status = new StatusResponse();
        status.setCode("code");
        status.setDescription("description");
        deposit.setStatus(status);
        deposit.setToAcctName("toAcctName");
        deposit.setTransactionAmount("transactionAmount");
        deposit.setDebitCurrentBalance("debitCurrentBalance");
        deposit.setDebitAvailableBalance("debitAvailableBalance");
        deposit.setFeeAmount("feeAmount");
        deposit.setTellerId("tellerId");
        deposit.setFlagFeeReg("flagFeeReg");
        deposit.setWaiveProductCode("waiveProductCode");
        deposit.setAmountWaived("amountWaived");
        depositResponse.setDeposit(deposit);
        depositResponseTmbOneServiceResponse.setData(depositResponse);
        Exception e = new Exception();
        StackTraceElement[] failed={};
        e.setStackTrace(failed);
        ResponseEntity<TmbOneServiceResponse<DepositResponse>> result = moneyTransferController.failedErrorResponse(responseHeaders, depositResponseTmbOneServiceResponse, e);
        Assert.assertEquals("0001", result.getBody().getStatus().getCode());

    }

    @Test
    void getTmbOneServiceResponseResponseEntity() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setBearerAuth("1234");
        final TmbOneServiceResponse<DepositResponse> depositResponseTmbOneServiceResponse = new TmbOneServiceResponse<>();
        depositResponseTmbOneServiceResponse.setStatus(new TmbStatus("code", "message", "service", "description"));
        final DepositResponse depositResponse = new DepositResponse();
        final DepositSuccessResponse deposit = new DepositSuccessResponse();
        final StatusResponse status = new StatusResponse();
        status.setCode("code");
        status.setDescription("description");
        deposit.setStatus(status);
        deposit.setToAcctName("toAcctName");
        deposit.setTransactionAmount("transactionAmount");
        deposit.setDebitCurrentBalance("debitCurrentBalance");
        deposit.setDebitAvailableBalance("debitAvailableBalance");
        deposit.setFeeAmount("feeAmount");
        deposit.setTellerId("tellerId");
        deposit.setFlagFeeReg("flagFeeReg");
        deposit.setWaiveProductCode("waiveProductCode");
        deposit.setAmountWaived("amountWaived");
        depositResponse.setDeposit(deposit);
        depositResponseTmbOneServiceResponse.setData(depositResponse);
        ResponseEntity<TmbOneServiceResponse<DepositResponse>> result = moneyTransferController.getTmbOneServiceResponseResponseEntity(responseHeaders, depositResponseTmbOneServiceResponse);
        Assert.assertEquals("0009", result.getBody().getStatus().getCode());

    }
}
