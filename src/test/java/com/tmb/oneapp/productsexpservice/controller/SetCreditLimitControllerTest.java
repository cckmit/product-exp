package com.tmb.oneapp.productsexpservice.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
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
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitReq;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitResp;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.Status;

@RunWith(JUnit4.class)
public class SetCreditLimitControllerTest {
	SetCreditLimitController setCreditLimitController;
	@Mock
	CreditCardClient creditCardClient;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		setCreditLimitController = new SetCreditLimitController(creditCardClient);

	}

	@Test
	void testSetCreditLimitsuccess() throws Exception {
		String correlationId = "c83936c284cb398fA46CF16F399C";
		SetCreditLimitReq requestBodyParameter = new SetCreditLimitReq();
		requestBodyParameter.setAccountId("0000000050078680266000215");
		Status status = new Status();
		status.setStatusCode("0");
		SetCreditLimitResp setCreditLimitResp = new SetCreditLimitResp();
		setCreditLimitResp.setStatus(status);
		TmbOneServiceResponse<SetCreditLimitResp> oneServiceResponse = new TmbOneServiceResponse<SetCreditLimitResp>();
		oneServiceResponse.setData(setCreditLimitResp);
		Map<String, String> requestHeadersParameter = headerRequestParameter();
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> response = new ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>>(
				oneServiceResponse, HttpStatus.OK);
		when(creditCardClient.fetchSetCreditLimit(anyString(), any(), any())).thenReturn(response);
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> res = setCreditLimitController
				.setCreditLimit(requestBodyParameter, requestHeadersParameter);
		assertEquals(200, res.getStatusCodeValue());

	}

	@Test
	void testSetCreditLimitError() throws Exception {
		String correlationId = "c83936c284cb398fA46CF16F399C";
		SetCreditLimitReq requestBodyParameter = new SetCreditLimitReq();
		requestBodyParameter.setAccountId("0000000050078680266000215");
		Status status = new Status();
		status.setStatusCode("0");
		SetCreditLimitResp setCreditLimitResp = new SetCreditLimitResp();
		setCreditLimitResp.setStatus(status);
		TmbOneServiceResponse<SetCreditLimitResp> oneServiceResponse = new TmbOneServiceResponse<SetCreditLimitResp>();
		oneServiceResponse.setData(setCreditLimitResp);
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> response = new ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>>(
				oneServiceResponse, HttpStatus.OK);
		Map<String, String> requestHeadersParameter = headerRequestParameter();
		when(creditCardClient.getCardBlockCode(anyString(), anyString())).thenThrow(RuntimeException.class);
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> res = setCreditLimitController
				.setCreditLimit(requestBodyParameter, requestHeadersParameter);
		assertNull(res.getBody().getData());

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
