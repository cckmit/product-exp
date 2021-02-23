package com.tmb.oneapp.productsexpservice.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.GetCardBlockCodeResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.GetCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeStatus;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.VerifyCreditCardResponse;

@RunWith(JUnit4.class)
public class CreditCardControllerTest {
	CreditCardController creditCardController;
	@Mock
	CreditCardClient creditCardClient;
	@Mock
	CreditCardLogService creditCardLogService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		creditCardController = new CreditCardController(creditCardClient, creditCardLogService);

	}

	@Test
	void testVerifyCreditCardDetailsSuccess() throws Exception {
		Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
				"0000000050078360018000167");
		GetCardBlockCodeResponse getCardBlockCodeResponse = new GetCardBlockCodeResponse();
		SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
		silverlakeStatus.setStatusCode(0);
		getCardBlockCodeResponse.setStatus(silverlakeStatus);
		GetCardResponse getCardResponse = new GetCardResponse();
		getCardResponse.setStatus(silverlakeStatus);
		handleGetCardBlockCodeResponse(getCardBlockCodeResponse, HttpStatus.OK);
		handleGetCardResponse(getCardResponse, HttpStatus.OK);

		ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> response = creditCardController
				.verifyCreditCardDetails(requestHeadersParameter);
		assertEquals(200, response.getStatusCodeValue());

	}

	@Test
	void testVerifyCreditCardDetailsNOData() throws Exception {
		Map<String, String> requestHeadersParameter = headerRequestParameter("", "");
		ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> response = creditCardController
				.verifyCreditCardDetails(requestHeadersParameter);
		assertEquals(400, response.getStatusCodeValue());

	}

	@Test
	void testVerifyCreditCardDetailsError() throws Exception {
		Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
				"0000000050078360018000167");
		when(creditCardClient.getCardBlockCode(anyString(), anyString())).thenThrow(RuntimeException.class);

		ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> response = creditCardController
				.verifyCreditCardDetails(requestHeadersParameter);
		assertNull(response.getBody().getData());

	}

	@Test
	void testVerifyCreditCardDetailsBlockCodeResNull() throws Exception {
		Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
				"0000000050078360018000167");
		GetCardBlockCodeResponse getCardBlockCodeResponse = null;
		handleGetCardBlockCodeResponse(getCardBlockCodeResponse, HttpStatus.BAD_REQUEST);

		ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> response = creditCardController
				.verifyCreditCardDetails(requestHeadersParameter);
		assertEquals(400, response.getStatusCodeValue());

	}

	@Test
	void testVerifyCreditCardDetailsCardResNull() throws Exception {
		Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
				"0000000050078360018000167");
		GetCardBlockCodeResponse getCardBlockCodeResponse = new GetCardBlockCodeResponse();
		SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
		silverlakeStatus.setStatusCode(0);
		getCardBlockCodeResponse.setStatus(silverlakeStatus);
		handleGetCardBlockCodeResponse(getCardBlockCodeResponse, HttpStatus.OK);
		GetCardResponse getCardResponse = null;
		handleGetCardResponse(getCardResponse, HttpStatus.BAD_REQUEST);
		ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> response = creditCardController
				.verifyCreditCardDetails(requestHeadersParameter);
		assertEquals(400, response.getStatusCodeValue());

	}

	public void handleGetCardBlockCodeResponse(GetCardBlockCodeResponse getCardBlockCodeResponse, HttpStatus status) {
		TmbOneServiceResponse<GetCardBlockCodeResponse> oneServiceResponse = new TmbOneServiceResponse<GetCardBlockCodeResponse>();
		oneServiceResponse.setData(getCardBlockCodeResponse);
		ResponseEntity<GetCardBlockCodeResponse> res = new ResponseEntity<GetCardBlockCodeResponse>(
				getCardBlockCodeResponse, status);
		when(creditCardClient.getCardBlockCode(anyString(), anyString())).thenReturn(res);

	}

	public void handleGetCardResponse(GetCardResponse getCardResponse, HttpStatus status) {
		TmbOneServiceResponse<GetCardResponse> oneServiceResponse = new TmbOneServiceResponse<GetCardResponse>();
		oneServiceResponse.setData(getCardResponse);
		ResponseEntity<GetCardResponse> getCardRes = new ResponseEntity<GetCardResponse>(getCardResponse, status);
		when(creditCardClient.getCreditCardDetails(anyString(), anyString())).thenReturn(getCardRes);

	}

	public Map<String, String> headerRequestParameter(String correlationId, String accountNo) {
		Map<String, String> reqData = new HashMap<String, String>();
		reqData.put(ProductsExpServiceConstant.X_CORRELATION_ID, correlationId);
		reqData.put(ProductsExpServiceConstant.ACCOUNT_ID, accountNo);
		return reqData;

	}

}
