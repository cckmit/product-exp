package com.tmb.oneapp.productsexpservice.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitReq;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;

@RunWith(JUnit4.class)
public class CreditCardLogServiceTest {

	KafkaProducerService kafkaProducerService;
	CreditCardLogService logService;
	CreditCardEvent creditCardEvent;
	Map headers = new HashMap<>();

	@BeforeEach
	void setUp() {

		creditCardEvent = new CreditCardEvent("", "", "");
		creditCardEvent.setCorrelationId("100");
		creditCardEvent.setActivityTypeId("101");
		creditCardEvent.setCardNumber("0000000050078360018000167");
		creditCardEvent.setResult("Success");
		creditCardEvent.setActivityTypeId("00700101");
		creditCardEvent.setActivityStatus(ProductsExpServiceConstant.SUCCESS);
		kafkaProducerService = mock(KafkaProducerService.class);
		logService = new CreditCardLogService("testTopic", kafkaProducerService);
		headers.put("account-id", "0000000050078360018000167");
		headers.put("activity-type-id", 00700101);
		headers.put("x-forward-for", "20.0.0.1");

	}

	@Test
	void testCallActivityBaseEvent() {

		CreditCardEvent result = logService.callActivityBaseEvent(creditCardEvent, headers);

		result.setResult(ProductsExpServiceConstant.SUCCESS);

		assertEquals("20.0.0.1", creditCardEvent.getIpAddress());
	}

	@Test
	void testVerifyCvvBaseEvent() {

		CreditCardEvent result = logService.verifyCvvBaseEvent(creditCardEvent, headers);

		result.setActivityStatus(ProductsExpServiceConstant.SUCCESS);

		assertEquals("00700101", creditCardEvent.getActivityTypeId());
	}

	@Test
	void testLogactivity() {
		CreditCardLogService cardLogSpy = Mockito.spy(logService);
		cardLogSpy.logActivity(creditCardEvent);
		verify(cardLogSpy, times(1)).logActivity(creditCardEvent);
	}

	@Test
	void testPopulateBaseEvents() {
		creditCardEvent.setIpAddress("10.0.2.2");
		creditCardEvent.setChannel(ProductsExpServiceConstant.CHANNEL);
		creditCardEvent.setAppVersion(ProductsExpServiceConstant.APP_VERSION);
		creditCardEvent.setDeviceModel("Apple");
		creditCardEvent.setActivityStatus(ProductsExpServiceConstant.SUCCESS);
		creditCardEvent.setCorrelationId("100");

		assertEquals("100", creditCardEvent.getCorrelationId());
		assertEquals("10.0.2.2", creditCardEvent.getIpAddress());
		assertEquals("Apple", creditCardEvent.getDeviceModel());
	}

	@Test
	void testCallVerifyCardNoEventSuccess() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("", "", "");
		Map<String, String> reqHeader = new HashMap<>();
		reqHeader.put(ProductsExpServiceConstant.X_FORWARD_FOR, "213123");
		reqHeader.put(ProductsExpServiceConstant.OS_VERSION, "1.2");
		reqHeader.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078680266000215");
		creditCardEvent = logService.callVerifyCardNoEvent(creditCardEvent, reqHeader);
		assertEquals(null, creditCardEvent.getDeviceModel());
	}
	
	@Test
	void testCompleteUsageListEventSuccess() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("", "", "");
		Map<String, String> reqHeader = new HashMap<>();
		reqHeader.put(ProductsExpServiceConstant.X_FORWARD_FOR, "213123");
		reqHeader.put(ProductsExpServiceConstant.OS_VERSION, "1.2");
		reqHeader.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078680266000215");
		SetCreditLimitReq requestBody = new  SetCreditLimitReq();
		requestBody.setAccountId("0000000050078680266000215");;
		creditCardEvent = logService.completeUsageListEvent(creditCardEvent, reqHeader, requestBody);
		assertEquals("Success", creditCardEvent.getActivityStatus());
	}
	
	@Test
	void testOnVerifyPinEventSuccess() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("", "", "");
		Map<String, String> reqHeader = new HashMap<>();
		reqHeader.put(ProductsExpServiceConstant.X_FORWARD_FOR, "213123");
		reqHeader.put(ProductsExpServiceConstant.OS_VERSION, "1.2");
		reqHeader.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078680266000215");
		creditCardEvent = logService.onVerifyPinEvent(creditCardEvent, reqHeader);
		assertEquals("Success", creditCardEvent.getActivityStatus());
	}
	
	@Test
	void testOnClickNextButtonEventSuccess() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("", "", "");
		Map<String, String> reqHeader = new HashMap<>();
		reqHeader.put(ProductsExpServiceConstant.X_FORWARD_FOR, "213123");
		reqHeader.put(ProductsExpServiceConstant.OS_VERSION, "1.2");
		reqHeader.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078680266000215");
		SetCreditLimitReq requestBody = new  SetCreditLimitReq();
		requestBody.setAccountId("0000000050078680266000215");;
		creditCardEvent = logService.onClickNextButtonEvent(creditCardEvent, reqHeader, requestBody);
		assertEquals(null, creditCardEvent.getChannel());
	}
	
	@Test
	void testOnClickNextButtonLimitEventPermanentSuccess() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("", "", "");
		Map<String, String> reqHeader = new HashMap<>();
		reqHeader.put(ProductsExpServiceConstant.X_FORWARD_FOR, "213123");
		reqHeader.put(ProductsExpServiceConstant.OS_VERSION, "1.2");
		reqHeader.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078680266000215");
		SetCreditLimitReq requestBody = new  SetCreditLimitReq();
		requestBody.setAccountId("0000000050078680266000215");
		creditCardEvent = logService.onClickNextButtonLimitEvent(creditCardEvent, reqHeader, requestBody, ProductsExpServiceConstant.MODE_PERMANENT);
		assertEquals(null, creditCardEvent.getChannel());
	}
	
	@Test
	void testOnClickNextButtonLimitEventTempSuccess() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("", "", "");
		Map<String, String> reqHeader = new HashMap<>();
		reqHeader.put(ProductsExpServiceConstant.X_FORWARD_FOR, "213123");
		reqHeader.put(ProductsExpServiceConstant.OS_VERSION, "1.2");
		reqHeader.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078680266000215");
		SetCreditLimitReq requestBody = new  SetCreditLimitReq();
		requestBody.setAccountId("0000000050078680266000215");
		creditCardEvent = logService.onClickNextButtonLimitEvent(creditCardEvent, reqHeader, requestBody, ProductsExpServiceConstant.MODE_TEMPORARY);
		assertEquals(null, creditCardEvent.getChannel());
	}
}