package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitReq;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitResp;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.Status;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class SetCreditLimitControllerTest {
	SetCreditLimitController setCreditLimitController;
	@Mock
	CreditCardClient creditCardClient;
	@Mock
	CreditCardLogService creditCardLogService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		setCreditLimitController = new SetCreditLimitController(creditCardClient, creditCardLogService);

	}

	@Test
	void testSetCreditLimitPermanentLimitSuccess() throws Exception {
		testLimit(ProductsExpServiceConstant.MODE_PERMANENT);
	}

	@Test
	void testSetCreditLimitTempLimitSuccess() throws Exception {
		testLimit(ProductsExpServiceConstant.MODE_TEMPORARY);

	}

	@Test
	void testSetCreditLimitNoLimitSuccess() throws Exception {
		testLimit("test");

	}

	void testLimit(String limit) {
		SetCreditLimitReq requestBodyParameter = new SetCreditLimitReq();
		requestBodyParameter.setAccountId("0000000050078680266000215");
		requestBodyParameter.setCurrentCreditLimit("1223");
		requestBodyParameter.setPreviousCreditLimit("222");
		requestBodyParameter.setExpiryDate("22");
		requestBodyParameter.setMode(limit);
		requestBodyParameter.setRequestReason("22");
		Status status = new Status();
		status.setStatusCode("0");
		Map<String, String> requestHeadersParameter = headerRequestParameter();
		SetCreditLimitResp setCreditLimitResp = new SetCreditLimitResp();
		setCreditLimitResp.setStatus(status);
		CreditCardEvent creditCardEvent = new CreditCardEvent(anyString(), anyString(), anyString());
		creditCardEvent.setActivityDate("01-09-1990");
		when(creditCardLogService.completeUsageListEvent(creditCardEvent, requestHeadersParameter,
				requestBodyParameter)).thenReturn(creditCardEvent);
		TmbOneServiceResponse<SetCreditLimitResp> oneServiceResponse = new TmbOneServiceResponse<SetCreditLimitResp>();
		oneServiceResponse.setData(setCreditLimitResp);
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> response = new ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>>(
				oneServiceResponse, HttpStatus.OK);
		when(creditCardClient.fetchSetCreditLimit(anyString(), any())).thenReturn(response);
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> res = setCreditLimitController
				.setCreditLimit(requestBodyParameter, requestHeadersParameter);
		assertEquals(200, res.getStatusCodeValue());
	}

	@Test
	void testSetCreditLimitError() throws Exception {
		SetCreditLimitReq requestBodyParameter = new SetCreditLimitReq();
		requestBodyParameter.setAccountId("0000000050078680266000215");
		requestBodyParameter.setCurrentCreditLimit("1223");
		requestBodyParameter.setPreviousCreditLimit("222");
		requestBodyParameter.setExpiryDate("22");
		requestBodyParameter.setMode("22");
		requestBodyParameter.setRequestReason("22");
		Status status = new Status();
		status.setStatusCode("0");
		SetCreditLimitResp setCreditLimitResp = new SetCreditLimitResp();
		setCreditLimitResp.setStatus(status);
		TmbOneServiceResponse<SetCreditLimitResp> oneServiceResponse = new TmbOneServiceResponse<SetCreditLimitResp>();
		oneServiceResponse.setData(setCreditLimitResp);
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> response = new ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>>(
				oneServiceResponse, HttpStatus.OK);
		Map<String, String> requestHeadersParameter = headerRequestParameter();
		CreditCardEvent creditCardEvent = new CreditCardEvent("123", "123", "1234");
		when(creditCardLogService.completeUsageListEvent(any(), any(), any())).thenReturn(creditCardEvent);
		when(creditCardClient.fetchSetCreditLimit(anyString(), any())).thenThrow(new
				IllegalStateException("Error occurred"));
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