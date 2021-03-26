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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cardInstallmentController = new CardInstallmentController(creditCardClient, creditCardLogService);
    }


    @Test
    public void testCardinstallmentResponseNull() throws Exception {
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
        TmbOneServiceResponse<CardInstallmentFinalResponse> response = new TmbOneServiceResponse();
        CardStatement cardStatement = new CardStatement();
        cardStatement.setDueDate("");
        CardInstallmentFinalResponse data = new CardInstallmentFinalResponse();
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
        response.setData(data);
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setCode("0");

        response.setStatus(tmbStatus);


        String activityId = ProductsExpServiceConstant.APPLY_SO_GOOD_ON_CLICK_CONFIRM_BUTTON;
        String activityDate = Long.toString(System.currentTimeMillis());
        CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, activityId, activityDate);
        creditCardEvent.setActivityDate("01-09-1990");
        when(creditCardClient.confirmCardInstallment(anyString(), any())).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        ResponseEntity<TmbOneServiceResponse<CardInstallmentFinalResponse>> cardInstallmentDetails = cardInstallmentController.confirmCardInstallment(correlationId, requestBodyParameter, headerRequestParameter());
        Assert.assertEquals(200, cardInstallmentDetails.getStatusCodeValue());
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
            cardInstallment.set(0, card);
        }
        requestBodyParameter.setCardInstallment(cardInstallment);
        TmbOneServiceResponse<CardInstallmentResponse> response = new TmbOneServiceResponse();
        CardStatement cardStatement = new CardStatement();
        cardStatement.setDueDate("");
        CardInstallmentResponse data = new CardInstallmentResponse();
        CardStatementReponse statement = new CardStatementReponse();
        statement.setStatementTransactions(list);
        data.setMaxRecords(100);
        data.setMoreRecords("Y");
        data.setTotalRecords(10);
        data.setCardStatement(statement);
        response.setData(data);
        TmbStatus status = new TmbStatus();
        status.setCode("0");
        status.setDescription("");
        status.setService("products experience");
        response.setStatus(status);
        String activityId = ProductsExpServiceConstant.APPLY_SO_GOOD_ON_CLICK_CONFIRM_BUTTON;
        String activityDate = Long.toString(System.currentTimeMillis());
        CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, activityId, activityDate);
        when(creditCardClient.confirmCardInstallment(anyString(), any())).thenThrow(FeignException.FeignClientException.class);
        Assertions.assertThrows(TMBCommonException.class,
                () -> cardInstallmentController.confirmCardInstallment(correlationId, requestBodyParameter, headerRequestParameter()));

    }


    public Map<String, String> headerRequestParameter() {
        Map<String, String> headers = new HashMap<>();
        headers.put(ProductsExpServiceConstant.X_CORRELATION_ID, "test");
        headers.put("os-version", "1.1");
        headers.put("device-model", "nokia");
        headers.put("activity-type-id", "00700103");
        return headers;

    }
}

