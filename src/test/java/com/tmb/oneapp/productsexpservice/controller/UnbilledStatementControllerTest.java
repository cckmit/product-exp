package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeStatus;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.CardStatement;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.GetUnbilledStatementQuery;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.StatementTransaction;
import com.tmb.oneapp.productsexpservice.model.response.buildstatement.BilledStatementResponse;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class UnbilledStatementControllerTest {

    @Mock
    CreditCardClient creditCardClient;

    @InjectMocks
    UnbilledStatementController unbilledStatementController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        unbilledStatementController = new UnbilledStatementController(creditCardClient);

    }

    @Test
    void getCreditCardDetailsSuccessShouldReturnGetCardResponseTest() {
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
        String accountId = "0000000050078680019000079";

        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        silverlakeStatus.setStatusCode(0);

        BilledStatementResponse getCardResponse = new BilledStatementResponse();
        getCardResponse.setStatus(silverlakeStatus);

        ResponseEntity value = new ResponseEntity(getCardResponse, HttpStatus.OK);
        Mockito.when(creditCardClient.getUnBilledStatement(any(), any())).thenReturn(value);

        GetUnbilledStatementQuery requestBody = new GetUnbilledStatementQuery();
        requestBody.setSearchKeys("");
        requestBody.setAccountId("0000000050078680472000929");
        requestBody.setMoreRecords("N");
        ResponseEntity<BilledStatementResponse> actual = creditCardClient.getUnBilledStatement(correlationId, requestBody);

        Integer statusCode = Objects.requireNonNull(actual.getBody()).getStatus().getStatusCode();
        Assertions.assertEquals(0, statusCode);
    }

    @Test
    public void testGetUnBilledStatement() {
        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        silverlakeStatus.setStatusCode(0);
        new BilledStatementResponse().setStatus(silverlakeStatus);
        new BilledStatementResponse().setCardStatement(new CardStatement());
        BilledStatementResponse billedStatementResponse = new BilledStatementResponse();
        billedStatementResponse.setStatus(silverlakeStatus);
        when(creditCardClient.getUnBilledStatement(anyString(), any())).thenReturn(new ResponseEntity<>(billedStatementResponse, HttpStatus.OK));

        ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> result = unbilledStatementController.getUnBilledStatement("correlationId", new GetUnbilledStatementQuery("accountId", "moreRecords", "searchKeys"));

        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    void getUnBilledStatementSuccessShouldReturnBilledStatementResponseTest() {
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
        String accountId = "0000000050078680472000929";

        ResponseEntity<BilledStatementResponse> value = getResponseEntity();

        Mockito.when(creditCardClient.getUnBilledStatement(any(), any())).thenReturn(value);

        GetUnbilledStatementQuery requestBody = new GetUnbilledStatementQuery();
        requestBody.setAccountId("0000000050078680472000929");
        requestBody.setMoreRecords("N");
        requestBody.setSearchKeys("");
        ResponseEntity<BilledStatementResponse> actual = creditCardClient.getUnBilledStatement(correlationId, requestBody);
        assertEquals(200, actual.getStatusCode().value());
    }

    private ResponseEntity<BilledStatementResponse> getResponseEntity() {
        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        silverlakeStatus.setStatusCode(0);
        BilledStatementResponse setCreditLimitResp = new BilledStatementResponse();
        setCreditLimitResp.setStatus(silverlakeStatus);
        setCreditLimitResp.setTotalRecords(10);
        setCreditLimitResp.setMaxRecords(100);
        setCreditLimitResp.setMoreRecords("100");
        setCreditLimitResp.setSearchKeys("N");
        CardStatement cardStatement = new CardStatement();
        cardStatement.setPromotionFlag("Y");
        setCreditLimitResp.setCardStatement(cardStatement);
        return new ResponseEntity<>(setCreditLimitResp, HttpStatus.OK);
    }


    private TmbOneServiceResponse<BilledStatementResponse> getBilledStatementResponse() {
        TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus status = new TmbStatus();
        status.setCode("0");
        status.setDescription("200 OK");
        status.setService("unbilled statement service");
        status.setMessage("Successful");
        oneServiceResponse.setStatus(status);
        BilledStatementResponse data = new BilledStatementResponse();
        data.setSearchKeys("N");
        data.setMoreRecords("Y");
        data.setMaxRecords(10);
        data.setTotalRecords(100);
        oneServiceResponse.setData(data);
        return oneServiceResponse;
    }

    @Test
    public void testHandlingFailedResponse() {
        TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse = getTmbOneServiceResponse();
        HttpHeaders responseHeaders = getHttpHeaders();
        when(creditCardClient.getUnBilledStatement(any(), any())
        ).thenThrow(new
                IllegalStateException("Error occurred"));
        ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> result = unbilledStatementController
                .handlingFailedResponse(oneServiceResponse, responseHeaders);

        Assert.assertEquals("0001", result.getBody().getStatus().getCode());
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_CORRELATION_ID, "123");
        return responseHeaders;
    }

    private TmbOneServiceResponse<BilledStatementResponse> getTmbOneServiceResponse() {
        TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        BilledStatementResponse setCreditLimitResp = getStatementResponse();
        CardStatement cardStatement = new CardStatement();
        cardStatement.setPromotionFlag("Y");
        setCreditLimitResp.setCardStatement(cardStatement);
        oneServiceResponse.setData(setCreditLimitResp);
        return oneServiceResponse;
    }

    private BilledStatementResponse getStatementResponse() {
        BilledStatementResponse setCreditLimitResp = new BilledStatementResponse();
        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        silverlakeStatus.setStatusCode(1);
        setCreditLimitResp.setStatus(silverlakeStatus);
        setCreditLimitResp.setTotalRecords(10);
        setCreditLimitResp.setMaxRecords(100);
        setCreditLimitResp.setMoreRecords("100");
        setCreditLimitResp.setSearchKeys("N");
        CardStatement cardStatement = new CardStatement();
        List<StatementTransaction> statementTransactions = new ArrayList<>();
        StatementTransaction transaction = new StatementTransaction();
        transaction.setTransactionCode(0);
        transaction.setTransactionDate("10-10-2020");
        transaction.setTransactionAmounts(BigDecimal.valueOf(1000.00));
        transaction.setTransactionKey("test");
        transaction.setTransactionType("test");
        transaction.setMccCode("test");
        transaction.setTransactionCurrency("test");
        transaction.setTransactionDescription("success");
        transaction.setPostedDate("test");
        cardStatement.setStatementTransactions(statementTransactions);
        setCreditLimitResp.setCardStatement(cardStatement);
        return setCreditLimitResp;
    }

    @Test
    void testBilledStatementError() {
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
        String accountId = "0000000050078680472000929";
        when(creditCardClient.getReasonList(anyString())).thenThrow(RuntimeException.class);
        GetUnbilledStatementQuery requestBody = new GetUnbilledStatementQuery();
        requestBody.setAccountId(accountId);
        requestBody.setMoreRecords("Y");
        requestBody.setSearchKeys("N");
        ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> billedStatement = unbilledStatementController.getUnBilledStatement(correlationId, requestBody);
        assertNull(billedStatement.getBody().getData());
    }

    @Test
    void testReasonListSuccessNull() {
        String correlationId = "c83936c284cb398fA46CF16F399C";

        ResponseEntity<BilledStatementResponse> response = null;
        when(creditCardClient.getUnBilledStatement(anyString(), any())).thenReturn(response);
        GetUnbilledStatementQuery requestBody = new GetUnbilledStatementQuery("0000000050078680472000929", "2", "N");
        ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> billedStatement = unbilledStatementController
                .getUnBilledStatement(correlationId, requestBody);
        assertEquals(400, billedStatement.getStatusCodeValue());

    }

    @Test
    public void testGetUnBilledStatementResp() {
        String correlationId = "c83936c284cb398fA46CF16F399C";
        BilledStatementResponse resp = getStatementResponse();
        ResponseEntity<BilledStatementResponse> response = new ResponseEntity<>(resp, HttpStatus.OK);
        when(creditCardClient.getUnBilledStatement(anyString(), any())).thenReturn(response);

        GetUnbilledStatementQuery requestBody = new GetUnbilledStatementQuery("accountId", "moreRecords", "searchKeys");
        ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> result = unbilledStatementController.getUnBilledStatement(correlationId, requestBody);
        Assert.assertNotEquals(null, result);
    }

    @Test
    public void testGetTmbOneServiceResponseResponse() {
        TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse = getTmbOneServiceResponse();
        HttpHeaders responseHeaders = getHttpHeaders();
        BilledStatementResponse resp = getStatementResponse();

        ResponseEntity<BilledStatementResponse> billedStatementResponse = new ResponseEntity<>(resp, HttpStatus.OK);
        when(creditCardClient.getUnBilledStatement(anyString(), any())).thenReturn(billedStatementResponse);
        ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> result = unbilledStatementController.getTmbOneServiceResponseResponse(oneServiceResponse, responseHeaders, billedStatementResponse);
        Assert.assertNotEquals(null, result);
    }

    @Test
    public void testHandlingFailedData() {
        TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse = getTmbOneServiceResponse();
        HttpHeaders responseHeaders = getHttpHeaders();
        ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> result = unbilledStatementController.handlingFailedResponse(oneServiceResponse, responseHeaders);
        Assert.assertNotEquals(null, result);
    }

    @Test
    public void testHandlingResponseData() {
        BilledStatementResponse resp = getStatementResponse();
        ResponseEntity<BilledStatementResponse> response = new ResponseEntity<>(resp, HttpStatus.OK);
        TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse = getTmbOneServiceResponse();
        HttpHeaders responseHeaders = getHttpHeaders();
        ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> result = unbilledStatementController.handlingResponseData(response, oneServiceResponse, responseHeaders);
        Assert.assertNotEquals(null, result);
    }

}

