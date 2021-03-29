package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.Status;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CampaignTransactionQuery;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CampaignTransactionResponse;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CardStatementReponse;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.StatementTransaction;
import feign.FeignException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CampaignTransactionsControllerTest {
    @Mock
    CreditCardClient creditCardClient;
    @InjectMocks
    CampaignTransactionsController campaignTransactionsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        campaignTransactionsController = new CampaignTransactionsController(creditCardClient);
    }

    @Test
    public void testCampaignTransactionResponse() throws Exception {
        String correlationId = "123";
        CampaignTransactionQuery requestBodyParameter = new CampaignTransactionQuery();
        requestBodyParameter.setAccountId("0000000050078670143000945");
        requestBodyParameter.setMoreRecords("N");
        requestBodyParameter.setSearchKeys("");
        TmbStatus status = new TmbStatus();
        status.setCode("0");
        Status stat = new Status();
        stat.setCode("0");
        CampaignTransactionResponse resp = new CampaignTransactionResponse();
        CardStatementReponse cardStatement = new CardStatementReponse();
        List<StatementTransaction> statementTransactions = new ArrayList<>();
        StatementTransaction transaction = new StatementTransaction();
        transaction.setTransactionCode(123);
        transaction.setTransactionDescription("Transaction is successful");
        statementTransactions.add(transaction);
        cardStatement.setStatementTransactions(statementTransactions);
        resp.setCardStatement(cardStatement);
        TmbOneServiceResponse<CampaignTransactionResponse> response = new TmbOneServiceResponse();
        response.setStatus(status);
        response.setData(resp);
       when(creditCardClient.getCampaignTransactionsDetails(anyString(), any())).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        Map<String, String> requestHeadersParameter= new HashMap();
        requestHeadersParameter.put(correlationId,"32fbd3b2-3f97-4a89-ar39-b4f628fbc8da");
        ResponseEntity<TmbOneServiceResponse<CampaignTransactionResponse>> result = campaignTransactionsController.campaignTransactionResponse(requestBodyParameter,requestHeadersParameter);
        Assert.assertEquals(200, result.getStatusCodeValue());
    }
    @Test
    void testCampaignTransactionResponseNoDataFound() throws Exception {
        String correlationId = "123";
        CampaignTransactionQuery requestBodyParameter = new CampaignTransactionQuery();
        requestBodyParameter.setAccountId("0000000050078670143000945");
        requestBodyParameter.setMoreRecords("N");
        requestBodyParameter.setSearchKeys("");
        Status status = new Status();
        status.setCode("0");
        Map<String, String> requestHeadersParameter= new HashMap();
        requestHeadersParameter.put(correlationId,"32fbd3b2-3f97-4a89-ar39-b4f628fbc8da");
        ResponseEntity<TmbOneServiceResponse<CampaignTransactionResponse>> res = campaignTransactionsController
                .campaignTransactionResponse(requestBodyParameter, requestHeadersParameter);
        assertEquals(400,res.getStatusCodeValue());

    }

    @Test
    void testBlockCardDetailsError() throws Exception {
        Map<String, String> requestHeadersParameter = new HashMap<>();
        requestHeadersParameter.put(ProductsExpServiceConstant.X_CORRELATION_ID, "test");
        CampaignTransactionQuery requestBodyParameter = new CampaignTransactionQuery();
        requestBodyParameter.setAccountId("0000000050078360018000167");
        requestBodyParameter.setSearchKeys("");
        requestBodyParameter.setMoreRecords("Y");
        CampaignTransactionResponse CampaignTransactionResponse = new CampaignTransactionResponse();
        Status status = new Status();
        status.setCode("0");
        status.setMessage("");
        status.setService("service name");
       // CampaignTransactionResponse.setStatus(status);
        when(creditCardClient.getCampaignTransactionsDetails(any(),any())).thenThrow(FeignException.FeignClientException.class);
        Assertions.assertThrows(TMBCommonException.class,
                () -> campaignTransactionsController.campaignTransactionResponse(requestBodyParameter, requestHeadersParameter));

    }
}
