package com.tmb.oneapp.productsexpservice.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.GetCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.GetCreditCardDetailsReq;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeStatus;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.VerifyCreditCardResponse;

@RunWith(JUnit4.class)
public class GetCardDetailsControllerTest {
	GetCardDetailsController getCardDetailsController;
	@Mock
	CreditCardClient creditCardClient;

	@BeforeEach

	void setUp() {
		MockitoAnnotations.initMocks(this);
		getCardDetailsController = new GetCardDetailsController(creditCardClient);

	}

	@Test
	void testfetchCardDetailsSuccess() throws Exception {
		String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
		GetCreditCardDetailsReq req = new GetCreditCardDetailsReq();
		req.setAccountId("0000000050078360018000167");
		SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
		silverlakeStatus.setStatusCode(0);
		GetCardResponse getCardResponse = new GetCardResponse();
		getCardResponse.setStatus(silverlakeStatus);
		handleGetCardRes(getCardResponse, HttpStatus.OK);
		ResponseEntity<TmbOneServiceResponse<GetCardResponse>> response = getCardDetailsController.fetchCardDetails(req,
				correlationId);
		assertEquals(200, response.getStatusCodeValue());

	}

	@Test
	void testfetchCardDetailsNoData() throws Exception {
		GetCreditCardDetailsReq req = new GetCreditCardDetailsReq();
		req.setAccountId("");
		ResponseEntity<TmbOneServiceResponse<GetCardResponse>> response = getCardDetailsController
				.fetchCardDetails(req, "");
		assertEquals(400, response.getStatusCodeValue());

	}

	public void handleGetCardRes(GetCardResponse getCardResponse, HttpStatus status) {
		TmbOneServiceResponse<GetCardResponse> oneServiceResponse = new TmbOneServiceResponse<GetCardResponse>();
		oneServiceResponse.setData(getCardResponse);
		ResponseEntity<GetCardResponse> getCardRes = new ResponseEntity<GetCardResponse>(getCardResponse, status);
		when(creditCardClient.getCreditCardDetails(anyString(), anyString())).thenReturn(getCardRes);

	}
	
	@Test
	void testfetchCardDetailsError() throws Exception {
		String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
		GetCreditCardDetailsReq req = new GetCreditCardDetailsReq();
		req.setAccountId("0000000050078360018000167");
		when(creditCardClient.getCardBlockCode(anyString(), anyString())).thenThrow(RuntimeException.class);

		ResponseEntity<TmbOneServiceResponse<GetCardResponse>> response = getCardDetailsController.fetchCardDetails(req,
				correlationId);
		assertNull(response.getBody().getData());

	}

}
