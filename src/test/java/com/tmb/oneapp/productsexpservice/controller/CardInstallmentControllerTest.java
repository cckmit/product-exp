
package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.*;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.CardStatement;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.StatementTransaction;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import com.tmb.oneapp.productsexpservice.service.NotificationService;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class CardInstallmentControllerTest {

    @Mock
    CreditCardClient creditCardClient;
    @InjectMocks
    CardInstallmentController cardInstallmentController;
    private List<StatementTransaction> list;
    @Mock
    private CreditCardLogService creditCardLogService;
    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cardInstallmentController = new CardInstallmentController(creditCardClient, creditCardLogService,
                notificationService);
    }

    @Test
    public void testCampaignTransactionResponseNull() throws Exception {
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
        CardInstallmentQuery requestBodyParameter = new CardInstallmentQuery();
        requestBodyParameter.setAccountId("0000000050078670143000945");
        CardInstallment card = new CardInstallment();
        card.setAmounts("5555.77");
        card.setModelType("IP");
        card.setTransactionKey("T0000020700000002");
        card.setPromotionModelNo("IPP001");

        List<CardInstallment> cardInstallment = new ArrayList();
        cardInstallment.add(card);

        requestBodyParameter.setCardInstallment(cardInstallment);
        TmbOneServiceResponse<List<CardInstallmentResponse>> response = new TmbOneServiceResponse<>();
        CardStatement cardStatement = new CardStatement();
        cardStatement.setDueDate("");
        CardInstallmentResponse data = new CardInstallmentResponse();
        CardStatementReponse statement = new CardStatementReponse();
        statement.setStatementTransactions(list);
        ErrorStatus errorStatus = new ErrorStatus();
        errorStatus.setErrorCode("1234");
        List<ErrorStatus> errorStatusList = new ArrayList<>();
        errorStatusList.add(errorStatus);
        StatusResponse status = new StatusResponse();
        status.setStatusCode("0");
        status.setErrorStatus(errorStatusList);
        data.setStatus(status);

        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setCode("0");

        response.setStatus(tmbStatus);

        String activityId = ProductsExpServiceConstant.APPLY_SO_GOOD_ON_CLICK_CONFIRM_BUTTON;
        String activityDate = Long.toString(System.currentTimeMillis());
        CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, activityId, activityDate);
        creditCardEvent.setActivityDate("01-09-1990");
        when(creditCardClient.confirmCardInstallment(anyString(), any()))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        ResponseEntity<TmbOneServiceResponse<List<CardInstallmentResponse>>> responseEntity = cardInstallmentController
                .confirmCardInstallment(correlationId, requestBodyParameter, headerRequestParameter());
        Assert.assertEquals("0", response.getStatus().getCode());
    }

    @Test
    void testBlockCardDetailsError() throws Exception {
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
        CardInstallmentQuery requestBodyParameter = new CardInstallmentQuery();
        requestBodyParameter.setAccountId("0000000050078670143000945");
        List<CardInstallment> cardInstallment = new ArrayList();
        for (CardInstallment installment : cardInstallment) {
            CardInstallment card = new CardInstallment();
            card.setAmounts("5555.77");
            card.setModelType("IP");
            card.setTransactionKey("T0000020700000002");
            card.setPromotionModelNo("IPP001");
            cardInstallment.add(installment);
        }
        requestBodyParameter.setCardInstallment(cardInstallment);
        TmbOneServiceResponse<CardInstallmentResponse> response = new TmbOneServiceResponse();
        CardStatement cardStatement = new CardStatement();
        cardStatement.setDueDate("");
        CardInstallmentResponse data = new CardInstallmentResponse();
        CardStatementReponse statement = new CardStatementReponse();
        statement.setStatementTransactions(list);
        CreditCardModel card = new CreditCardModel();
        card.setAccountId("0000000050078670143000945");
        CardInstallmentModel model = new CardInstallmentModel();
        model.setOrderNo("1234");
        model.setAmounts(1234.00);
        card.setCardInstallment(model);
        data.setCreditCard(card);

        response.setData(data);
        TmbStatus status = new TmbStatus();
        status.setCode("0");
        status.setDescription("");
        status.setService("products experience");
        response.setStatus(status);
        when(creditCardClient.confirmCardInstallment(anyString(), any()))
                .thenThrow(FeignException.FeignClientException.class);
        Assertions.assertThrows(TMBCommonException.class, () -> cardInstallmentController
                .confirmCardInstallment(correlationId, requestBodyParameter, headerRequestParameter()));

    }

    public Map<String, String> headerRequestParameter() {
        Map<String, String> headers = new HashMap<>();
        headers.put(ProductsExpServiceConstant.X_CORRELATION_ID, "test");
        headers.put("os-version", "1.1");
        headers.put("device-model", "nokia");
        headers.put("activity-type-id", "00700103");
        return headers;

    }

    @Test
    public void testHandlingFailedResponse() {
        TmbOneServiceResponse<List<CardInstallmentResponse>> oneServiceResponse = new TmbOneServiceResponse<>();
        List<CardInstallmentResponse> cardInstallment = new ArrayList();
        for (CardInstallmentResponse installment : cardInstallment) {
            CardInstallment card = new CardInstallment();
            card.setAmounts("5555.77");
            card.setModelType("IP");
            card.setTransactionKey("T0000020700000002");
            card.setPromotionModelNo("IPP001");
            cardInstallment.add(installment);
        }
        StatusResponse status = new StatusResponse();
        status.setStatusCode("1243");

        CreditCardModel creditLimit = new CreditCardModel();

        creditLimit.setAccountId("1234");
        CardInstallmentModel installment = new CardInstallmentModel();
        installment.setAmounts(1234.00);
        installment.setOrderNo("1234");
        installment.setTransactionKey("1234");
        installment.setTransactionDescription("TEST");
        creditLimit.setCardInstallment(installment);
        CardStatement cardStatement = new CardStatement();
        cardStatement.setPromotionFlag("Y");
        oneServiceResponse.setData(cardInstallment);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_CORRELATION_ID, "123");
        when(creditCardClient.confirmCardInstallment(any(), any()))
                .thenThrow(new IllegalStateException("Error occurred"));
        final TmbOneServiceResponse<List<CardInstallmentResponse>> loanStatementResponse = new TmbOneServiceResponse();
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setCode("0");
        tmbStatus.setDescription("Success");
        tmbStatus.setMessage("Success");
        tmbStatus.setService("loan-statement-service");
        loanStatementResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<List<CardInstallmentResponse>>> result = cardInstallmentController
                .populateErrorResponse(responseHeaders, oneServiceResponse, loanStatementResponse);
        Assert.assertEquals("0001", result.getBody().getStatus().getCode());
    }

    @Test
    public void testpopulateErrorResponse() {
        TmbOneServiceResponse<List<CardInstallmentResponse>> oneServiceResponse = new TmbOneServiceResponse<>();
        List<CardInstallmentResponse> cardInstallment = new ArrayList();
        for (CardInstallmentResponse installment : cardInstallment) {
            CardInstallment card = new CardInstallment();
            card.setAmounts("5555.77");
            card.setModelType("IP");
            card.setTransactionKey("T0000020700000002");
            card.setPromotionModelNo("IPP001");
            cardInstallment.add(installment);
        }
        StatusResponse status = new StatusResponse();
        status.setStatusCode("1243");

        CreditCardModel creditLimit = new CreditCardModel();

        creditLimit.setAccountId("1234");
        CardInstallmentModel installment = new CardInstallmentModel();
        installment.setAmounts(1234.00);
        installment.setOrderNo("1234");
        installment.setTransactionKey("1234");
        installment.setTransactionDescription("TEST");
        creditLimit.setCardInstallment(installment);
        CardStatement cardStatement = new CardStatement();
        cardStatement.setPromotionFlag("Y");
        oneServiceResponse.setData(cardInstallment);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_CORRELATION_ID, "123");
        when(creditCardClient.confirmCardInstallment(any(), any()))
                .thenThrow(new IllegalStateException("Error occurred"));
        final TmbOneServiceResponse<List<CardInstallmentResponse>> loanStatementResponse = new TmbOneServiceResponse();
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setCode("0");
        tmbStatus.setDescription("Success");
        tmbStatus.setMessage("Success");
        tmbStatus.setService("loan-statement-service");
        loanStatementResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<List<CardInstallmentResponse>>> result = cardInstallmentController
                .dataNotFoundErrorResponse(responseHeaders, oneServiceResponse);
        Assert.assertEquals("0009", result.getBody().getStatus().getCode());
    }

    @Test
    void ifSuccessCaseMatch() {
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
        CardInstallmentQuery requestHeadersParameter = new CardInstallmentQuery();
        List<CardInstallment> cardInstallment = new ArrayList<>();
        for (CardInstallment installment : cardInstallment) {
            installment.setMonthlyInstallments("12");
            installment.setInterest("12");
            installment.setTransactionKey("12");
            installment.setModelType("Test");
            installment.setAmounts("1234");
            installment.setTransactionDescription("Test");
            cardInstallment.add(installment);
        }
        requestHeadersParameter.setCardInstallment(cardInstallment);
        requestHeadersParameter.setCardInstallment(cardInstallment);
        Map<String, String> responseHeaders = headerRequestParameter();
        HttpHeaders oneServiceResponse = new HttpHeaders();
        oneServiceResponse.set("content-type", "application/json");
        TmbOneServiceResponse<List<CardInstallmentResponse>> data = new TmbOneServiceResponse();
        TmbStatus status = getTmbStatus();
        data.setStatus(status);

        TmbOneServiceResponse<List<CardInstallmentResponse>> cardInstallmentResp = new TmbOneServiceResponse();
        List<CardInstallmentResponse> res = new ArrayList<>();
        for (CardInstallmentResponse resp : res) {
            StatusResponse statusResponse = new StatusResponse();
            statusResponse.setStatusCode("0");
            resp.setStatus(statusResponse);
            res.add(resp);
        }
        cardInstallmentResp.setStatus(status);
        cardInstallmentResp.setData(res);
        assertNotNull(cardInstallmentController.ifSuccessCaseMatch(correlationId, requestHeadersParameter,
                responseHeaders, data, data, res));

    }

    private TmbStatus getTmbStatus() {
        TmbStatus status = new TmbStatus();
        status.setDescription("test");
        status.setService("card-installment-service");
        status.setCode("0");
        status.setMessage("test");
        return status;
    }

    @Test
    void testSuccessResponse() {
        TmbOneServiceResponse<List<CardInstallmentResponse>> response = new TmbOneServiceResponse<>();
        TmbStatus status = getTmbStatus();
        response.setStatus(status);
        TmbOneServiceResponse<List<CardInstallmentResponse>> cardInstallmentResp = response;
        cardInstallmentController.successResponse(response, cardInstallmentResp);
        assertNotNull(response);
    }


}
