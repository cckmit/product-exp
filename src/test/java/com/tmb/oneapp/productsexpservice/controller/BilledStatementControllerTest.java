package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeStatus;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.CardStatement;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.GetBilledStatementQuery;
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
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
@RunWith(JUnit4.class)
public class BilledStatementControllerTest {

    @Mock
    CreditCardClient creditCardClient;
    @Mock
    TMBLogger<UnbilledStatementController> logger;


    @InjectMocks
    BilledStatementController billedStatementController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        billedStatementController = new BilledStatementController(creditCardClient);

    }


    @org.junit.jupiter.api.Test
    public void testGetUnBilledStatement()  {
        SilverlakeStatus silverlakeStatus= new SilverlakeStatus();
        silverlakeStatus.setStatusCode(0);
        new BilledStatementResponse().setStatus(silverlakeStatus);
        new BilledStatementResponse().setCardStatement(new CardStatement());
        BilledStatementResponse billedStatementResponse = new BilledStatementResponse();
        billedStatementResponse.setStatus(silverlakeStatus);
        when(creditCardClient.getUnBilledStatement(anyString(), anyString())).thenReturn(new ResponseEntity<>(billedStatementResponse, HttpStatus.OK));

        ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> result = billedStatementController.getBilledStatement("correlationId", new GetBilledStatementQuery("accountId", "periodStatement", "cardId", "moreRecords", "searchKeys"));

        TmbOneServiceResponse<BilledStatementResponse> body = result.getBody();

        BilledStatementResponse data = new BilledStatementResponse();
        data.setStatus(silverlakeStatus);
        assertEquals(new Integer(0),data.getStatus().getStatusCode());
    }

  

    @Test
    public void testHandlingFailedResponse()  {
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
        responseHeaders.set(ProductsExpServiceConstant.HEADER_CORRELATION_ID,"123");
        when(creditCardClient.getUnBilledStatement(any(),any())).thenThrow(new
                IllegalStateException("Error occurred"));
        ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> result = billedStatementController
                .handlingFailedResponse(oneServiceResponse,responseHeaders);

        Assert.assertEquals("0001", result.getBody().getStatus().getCode());
    }
}

