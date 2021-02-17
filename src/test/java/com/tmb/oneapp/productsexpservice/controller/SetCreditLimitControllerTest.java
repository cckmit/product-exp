package com.tmb.oneapp.productsexpservice.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> response = new ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>>(
				oneServiceResponse, HttpStatus.OK);
		when(creditCardClient.fetchSetCreditLimit(anyString(), any())).thenReturn(response);
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> res = setCreditLimitController
				.setCreditLimit(requestBodyParameter, correlationId);
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
		when(creditCardClient.getCardBlockCode(anyString(), anyString())).thenThrow(RuntimeException.class);
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> res = setCreditLimitController
				.setCreditLimit(requestBodyParameter, correlationId);
		assertNull(res.getBody().getData());

	}

}
