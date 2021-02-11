package com.tmb.oneapp.productsexpservice.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ActivateCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeErrorStatus;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeStatus;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.VerifyCvvResponse;

public class ProductsVerifyCvvControllerTest {

	ProductsVerifyCvvController productsVerifyCvvController;
	@Mock
	CreditCardClient creditCardClient;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		productsVerifyCvvController = new ProductsVerifyCvvController(creditCardClient);

	}

	@Test
	void testActivateCardDetailsSuccess() throws Exception {
		Map<String, String> reqHeaders = headerRequestParameter("c83936c284cb398fA46CF16F399C",
				"0000000050078360018000167", "896", "2506");
		VerifyCvvResponse verifyCvvResponse = new VerifyCvvResponse();
		SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
		SilverlakeErrorStatus silverlakeErrorStatus = new SilverlakeErrorStatus();
		silverlakeErrorStatus.setDescription("test");
		silverlakeErrorStatus.setErrorCode("0");
		List<SilverlakeErrorStatus> errorStatus = new ArrayList<SilverlakeErrorStatus>();
		silverlakeStatus.setStatusCode(0);
		silverlakeStatus.setErrorStatus(errorStatus);
		verifyCvvResponse.setStatus(silverlakeStatus);
		handleGetCardResponse(verifyCvvResponse, HttpStatus.OK);
		ResponseEntity<TmbOneServiceResponse<ActivateCardResponse>> activateCardResponse = productsVerifyCvvController
				.verifyCvv(reqHeaders);
		assertEquals(200, activateCardResponse.getStatusCodeValue());

	}

	public Map<String, String> headerRequestParameter(String correlationId, String accountNo, String cvv,
			String cardExpiry) {
		Map<String, String> headers = new HashMap<>();
		headers.put(ProductsExpServiceConstant.HEADER_CORRELATION_ID, "c83936c284cb398fA46CF16F399C");
		headers.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078360018000167");
		headers.put(ProductsExpServiceConstant.CVV, "896");
		headers.put(ProductsExpServiceConstant.CARD_EXPIRY, "2506");
		return headers;

	}

	public void handleGetCardResponse(VerifyCvvResponse verifyCvvResponse, HttpStatus status) {
		TmbOneServiceResponse<VerifyCvvResponse> oneServiceResponse = new TmbOneServiceResponse<VerifyCvvResponse>();
		oneServiceResponse.setData(verifyCvvResponse);
		ResponseEntity<VerifyCvvResponse> getCardRes = new ResponseEntity<VerifyCvvResponse>(verifyCvvResponse, status);
		when(creditCardClient.verifyCvv(any())).thenReturn(getCardRes);

	}

}
