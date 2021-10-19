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

import com.tmb.common.model.StatusResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.creditcard.SetCreditLimitResp;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitReq;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.service.CacheService;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import com.tmb.oneapp.productsexpservice.service.NotificationService;

@RunWith(JUnit4.class)
public class SetCreditLimitControllerTest {
	SetCreditLimitController setCreditLimitController;
	@Mock
	CreditCardClient creditCardClient;
	@Mock
	CreditCardLogService creditCardLogService;
	@Mock
	NotificationService notificationService;
	@Mock
	CacheService cacheService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		setCreditLimitController = new SetCreditLimitController(creditCardClient, creditCardLogService,
				notificationService, cacheService);

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
		StatusResponse status = new StatusResponse();
		status.setStatusCode("0");
		Map<String, String> requestHeadersParameter = headerRequestParameter();
		requestHeadersParameter.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, "c28f91e4-881e-4387-a597-4a39c2822b3c");
		String correlationId = requestHeadersParameter.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String activityDate = Long.toString(System.currentTimeMillis());
		SetCreditLimitResp setCreditLimitResp = new SetCreditLimitResp();
		setCreditLimitResp.setStatus(status);
		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, activityDate,
				ProductsExpServiceConstant.CHANGE_TEMP_COMPLETE_ADJUST_USAGE_LIMIT);
		when(creditCardLogService.completeUsageListEvent(any(), any(), any(),
				any())).thenReturn(creditCardEvent);
		TmbOneServiceResponse<SetCreditLimitResp> oneServiceResponse = new TmbOneServiceResponse<SetCreditLimitResp>();
		oneServiceResponse.setData(setCreditLimitResp);
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> response = new ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>>(
				oneServiceResponse, HttpStatus.OK);
		when(creditCardClient.setCreditLimit(any(), any())).thenReturn(response);
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
		StatusResponse status = new StatusResponse();
		status.setStatusCode("0");
		SetCreditLimitResp setCreditLimitResp = new SetCreditLimitResp();
		setCreditLimitResp.setStatus(status);
		CreditCardEvent creditCardTempLimit = new CreditCardEvent("c83936c284cb398fA46CF16F399C", "03/09/2021",
				"00700201");
		TmbOneServiceResponse<SetCreditLimitResp> oneServiceResponse = new TmbOneServiceResponse<SetCreditLimitResp>();
		oneServiceResponse.setData(setCreditLimitResp);
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> response = new ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>>(
				oneServiceResponse, HttpStatus.OK);
		Map<String, String> requestHeadersParameter = headerRequestParameter();
		CreditCardEvent creditCardEvent = new CreditCardEvent("123", "123", "1234");
		when(creditCardLogService.completeUsageListEvent(creditCardEvent, requestHeadersParameter, requestBodyParameter,
				ProductsExpServiceConstant.SUCCESS))
				.thenReturn(creditCardEvent);
		when(creditCardLogService.onClickNextButtonLimitEvent(any(), any(), any(), anyString()))
				.thenReturn(creditCardTempLimit);
		ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> res = setCreditLimitController
				.setCreditLimit(requestBodyParameter, requestHeadersParameter);
		assertNull(res.getBody().getData());

	}

	public Map<String, String> headerRequestParameter() {
		Map<String, String> headers = new HashMap<>();
		headers.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, "test");
		headers.put("os-version", "1.1");
		headers.put("device-model", "nokia");
		headers.put("activity-type-id", "00700103");
		return headers;

	}

}