package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.Reason;
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
    void getCreditCardDetailsSuccessShouldReturnGetCardResponseTest()  {
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
        String accountId = "0000000050078680019000079";

        SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
        silverlakeStatus.setStatusCode(0);

        BilledStatementResponse getCardResponse = new BilledStatementResponse();
        getCardResponse.setStatus(silverlakeStatus);

        Mockito.when(creditCardClient.getUnBilledStatement(correlationId,accountId)).thenReturn(new ResponseEntity(getCardResponse,HttpStatus.OK ));

        ResponseEntity<BilledStatementResponse> actual = creditCardClient.getUnBilledStatement(correlationId, accountId);

        Assertions.assertEquals(0, Objects.requireNonNull(actual.getBody()).getStatus().getStatusCode());
    }

    @Test
    public void testGetUnBilledStatement()  {       
        SilverlakeStatus silverlakeStatus= new SilverlakeStatus();
       silverlakeStatus.setStatusCode(0);
        new BilledStatementResponse().setStatus(silverlakeStatus);
        new BilledStatementResponse().setCardStatement(new CardStatement());
        BilledStatementResponse billedStatementResponse = new BilledStatementResponse();
       billedStatementResponse.setStatus(silverlakeStatus);
       when(creditCardClient.getUnBilledStatement(anyString(), anyString())).thenReturn(new ResponseEntity<>(billedStatementResponse, HttpStatus.OK));

       ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> result = unbilledStatementController.getUnBilledStatement("correlationId", new GetBilledStatementQuery("accountId", "periodStatement","moreRecords", "searchKeys"));

        assertEquals(400,result.getStatusCode().value());
    }

    @Test
    void getUnBilledStatementSuccessShouldReturnBilledStatementResponseTest()  {
        String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
        String accountId = "0000000050078680472000929";

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
        ResponseEntity<BilledStatementResponse> value = new ResponseEntity<>(setCreditLimitResp,HttpStatus.OK);

        Mockito.when(creditCardClient.getUnBilledStatement(any(),any())).thenReturn(value);

        ResponseEntity<BilledStatementResponse> actual = creditCardClient.getUnBilledStatement(correlationId, accountId);
        assertEquals(200, actual.getStatusCode().value());
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
        ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> result = unbilledStatementController
                .handlingFailedResponse(oneServiceResponse,responseHeaders);

        Assert.assertEquals("0001", result.getBody().getStatus().getCode());
    }
    
	@Test
	void testBilledStatementError() throws Exception {
		 String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
	     String accountId = "0000000050078680472000929";
		when(creditCardClient.getReasonList(anyString())).thenThrow(RuntimeException.class);
		GetBilledStatementQuery requestBody = new GetBilledStatementQuery();
		requestBody.setAccountId(accountId);
		requestBody.setMoreRecords("Y");
		requestBody.setPeriodStatement("2");
		requestBody.setSearchKeys("N");
	    ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> billedStatement = unbilledStatementController.getUnBilledStatement(correlationId, requestBody);
		assertNull(billedStatement.getBody().getData());
	}
	
	@Test
	void testReasonListSuccessNull() throws Exception {
		String correlationId = "c83936c284cb398fA46CF16F399C";

        ResponseEntity<BilledStatementResponse> response=null;
        when(creditCardClient.getUnBilledStatement(anyString(),any())).thenReturn(response);
		GetBilledStatementQuery requestBody = new GetBilledStatementQuery("0000000050078680472000929","Y","2","N");
		 ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> billedStatement = unbilledStatementController
				.getUnBilledStatement(correlationId, requestBody);
		assertEquals(400, billedStatement.getStatusCodeValue());

	}
}

