package com.tmb.oneapp.productsexpservice.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
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
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ActivateCardResponse;

@RunWith(JUnit4.class)
public class ProductsActivateCardControllerTest {
	ProductsActivateCardController productsActivateCardController;
	@Mock
	CreditCardClient creditCardClient;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		productsActivateCardController = new ProductsActivateCardController(creditCardClient);

	}

	@Test
	void testActivateCardDetailsSuccess() throws Exception {
		Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
				"0000000050078360018000167");
		ActivateCardResponse activateCardResponse = new ActivateCardResponse();
		activateCardResponse.setAccountId("0000000050078360018000167");
		handleGetCardBlockCodeResponse(activateCardResponse, HttpStatus.OK);
		ResponseEntity<TmbOneServiceResponse<ActivateCardResponse>> res = productsActivateCardController
				.activateCard(requestHeadersParameter);
		assertEquals(200, res.getStatusCodeValue());

	}

	@Test
	void testActivateCardDetailsDataNotFound() throws Exception {
		Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
				"0000000050078360018000167");
		ActivateCardResponse activateCardResponse = new ActivateCardResponse();
		activateCardResponse.setAccountId("0000000050078360018000167");
		handleGetCardBlockCodeResponse(activateCardResponse, HttpStatus.BAD_REQUEST);
		ResponseEntity<TmbOneServiceResponse<ActivateCardResponse>> res = productsActivateCardController
				.activateCard(requestHeadersParameter);
		assertEquals(400, res.getStatusCodeValue());

	}

	@Test
	void testActivateCardDetailsDataError() throws Exception {
		Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
				"0000000050078360018000167");
		when(creditCardClient.activateCard(any())).thenThrow(RuntimeException.class);
		ResponseEntity<TmbOneServiceResponse<ActivateCardResponse>> res = productsActivateCardController
				.activateCard(requestHeadersParameter);
		assertNull(res.getBody().getData());

	}

	public Map<String, String> headerRequestParameter(String correlationId, String accountNo) {
		Map<String, String> reqData = new HashMap<String, String>();
		reqData.put(ProductsExpServiceConstant.X_CORRELATION_ID, correlationId);
		reqData.put(ProductsExpServiceConstant.ACCOUNT_ID, accountNo);
		return reqData;

	}

	public void handleGetCardBlockCodeResponse(ActivateCardResponse activateCardResponse, HttpStatus status) {
		TmbOneServiceResponse<ActivateCardResponse> oneServiceResponse = new TmbOneServiceResponse<ActivateCardResponse>();
		oneServiceResponse.setData(activateCardResponse);
		ResponseEntity<ActivateCardResponse> response = new ResponseEntity<ActivateCardResponse>(activateCardResponse,
				status);
		when(creditCardClient.activateCard(any())).thenReturn(response);

	}

}
