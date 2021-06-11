package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.model.CardInstallment;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.*;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.*;
import com.tmb.oneapp.productsexpservice.model.loan.Account;
import com.tmb.oneapp.productsexpservice.model.loan.HomeLoanFullInfoResponse;
import com.tmb.oneapp.productsexpservice.util.ConversionUtil;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

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
		creditCardEvent.setActivityDate("28-03-2021");
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
		SetCreditLimitReq requestBody = new SetCreditLimitReq();
		requestBody.setAccountId("0000000050078680266000215");
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
		SetCreditLimitReq requestBody = new SetCreditLimitReq();
		requestBody.setAccountId("0000000050078680266000215");
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
		SetCreditLimitReq requestBody = new SetCreditLimitReq();
		requestBody.setAccountId("0000000050078680266000215");
		creditCardEvent = logService.onClickNextButtonLimitEvent(creditCardEvent, reqHeader, requestBody,
				ProductsExpServiceConstant.MODE_PERMANENT);
		assertEquals(null, creditCardEvent.getChannel());
	}

	@Test
	void testOnClickNextButtonLimitEventTempSuccess() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("", "", "");
		Map<String, String> reqHeader = new HashMap<>();
		reqHeader.put(ProductsExpServiceConstant.X_FORWARD_FOR, "213123");
		reqHeader.put(ProductsExpServiceConstant.OS_VERSION, "1.2");
		reqHeader.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078680266000215");
		SetCreditLimitReq requestBody = new SetCreditLimitReq();
		requestBody.setAccountId("0000000050078680266000215");
		creditCardEvent = logService.onClickNextButtonLimitEvent(creditCardEvent, reqHeader, requestBody,
				ProductsExpServiceConstant.MODE_TEMPORARY);
		assertEquals(null, creditCardEvent.getChannel());
	}

	@Test
	void testApplySoGoodConfirmEvent() {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		Map<String, String> reqHeader = new HashMap<>();
		reqHeader.put(ProductsExpServiceConstant.X_FORWARD_FOR, "213123");
		reqHeader.put(ProductsExpServiceConstant.OS_VERSION, "1.2");
		reqHeader.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078680266000215");
		SetCreditLimitReq requestBody = new SetCreditLimitReq();
		requestBody.setAccountId("0000000050078680266000215");
		CardInstallmentQuery query = new CardInstallmentQuery();
		query.setAccountId("0000000050078670143000945");

		List<CardInstallment> cardInstallment = new ArrayList<>();
		for (CardInstallment installment : cardInstallment) {
			installment.setAmounts("1234");
			installment.setModelType("12334");
			installment.setInterest("343");
			installment.setTransactionKey("ABC1234");
			installment.setMonthlyInstallments("455");
			installment.setPromotionModelNo("IP0001");
			cardInstallment.add(installment);
		}
		query.setCardInstallment(cardInstallment);
		List<CardInstallmentResponse> installment = new ArrayList<>();
		CardInstallmentResponse response = new CardInstallmentResponse();
		StatusResponse status = new StatusResponse();
		status.setStatusCode("0");
		status.setErrorStatus(null);
		response.setStatus(status);
		installment.add(response);
		logService.generateApplySoGoodConfirmEvent(correlationId, reqHeader, query, installment);
		assertEquals("0", status.getStatusCode());

	}

	@Test
	public void testApplySoGoodConfirmEventList() {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String activityDate = "28-03-2021";
		String activityTypeId = "00700700";
		String accountId = "0000000050078680266000215";
		Map<String, String> hashMap = new HashMap();
		hashMap.put("1", "creditCard");
		hashMap.put("2", "debitCard");
		CardInstallmentQuery query = new CardInstallmentQuery();
		query.setAccountId(accountId);
		List<CardInstallment> cardInstallment = new ArrayList<>();
		for (CardInstallment installment : cardInstallment) {
			installment.setAmounts("1234");
			installment.setModelType("12334");
			installment.setInterest("343");
			installment.setTransactionKey("ABC1234");
			installment.setMonthlyInstallments("455");
			installment.setPromotionModelNo("IP0001");
			cardInstallment.add(installment);
		}
		query.setCardInstallment(cardInstallment);
		List<CardInstallmentResponse> installment = new ArrayList<>();
		CardInstallmentResponse response = getCardInstallmentResponse();
		installment.add(response);
		logService.generateApplySoGoodConfirmEvent(correlationId, hashMap, query, installment);
		assertEquals(false, Arrays.asList(new CreditCardEvent(correlationId, activityDate, activityTypeId)).isEmpty());
	}

	private CardInstallmentResponse getCardInstallmentResponse() {
		CardInstallmentResponse response = new CardInstallmentResponse();
		StatusResponse status = new StatusResponse();
		status.setStatusCode("0");
		status.setErrorStatus(null);
		CreditCardModel creditCard = getCreditCardModel();

		response.setCreditCard(creditCard);
		response.setStatus(status);
		return response;
	}

	@Test
	public void testLogActivityList() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", "28-03-2021",
				"00700700");
		assertNotNull(creditCardEvent);
	}

	@Test
	public void testFinishBlockCardActivityLog() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("", "", "");
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String activityDate = "28-03-2021";
		String activityId = "00700700";
		String status = "success";
		String accountId = "0000000050078680266000215";
		String failReason = "Exception";
		logService.finishBlockCardActivityLog(status, activityId, correlationId, activityDate, accountId, failReason);
		assertNotNull(creditCardEvent);
	}

	@Test
	public void testFinishSetPinActivityLog() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("", "", "");
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String activityDate = "28-03-2021";
		String activityId = "00700700";
		String status = "success";
		String accountId = "0000000050078680266000215";
		String failReason = "Exception";
		logService.finishSetPinActivityLog(status, activityId, correlationId, activityDate, accountId, failReason);
		assertNotNull(creditCardEvent);
	}

	@Test
	public void testLoadCardDetailsEvent() {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String activityDate = "28-03-2021";
		String activityId = ProductsExpServiceConstant.APPLY_SO_GOOD_ON_CLICK_CONFIRM_BUTTON;
		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, activityDate, activityId);
		Map<String, String> hashMap = new HashMap();
		hashMap.put("1", "creditCard");
		hashMap.put("2", "debitCard");
		SilverlakeStatus silverlakeStatus = new SilverlakeStatus();
		silverlakeStatus.setStatusCode(0);
		FetchCardResponse fetchCardResponse = new FetchCardResponse();
		fetchCardResponse.setStatus(silverlakeStatus);
		CreditCardDetail creditCard = new CreditCardDetail();
		creditCard.setAccountId("0000000050078670143000945");
		CardCreditLimit cardCreditLimit = new CardCreditLimit();
		cardCreditLimit.setPermanentCreditLimit(1000l);
		TemporaryCreditLimit tempCreditLimit = new TemporaryCreditLimit();
		tempCreditLimit.setAmounts(BigDecimal.valueOf(12232433.55));
		tempCreditLimit.setEffectiveDate("10-10-2020");
		cardCreditLimit.setTemporaryCreditLimit(tempCreditLimit);
		creditCard.setCardCreditLimit(cardCreditLimit);
		fetchCardResponse.setCreditCard(creditCard);
		creditCardEvent.setCardNumber("1234");
		creditCardEvent.setProductName("Tiger");
		ProductCodeData data = new ProductCodeData();
		data.setProductNameEN("Tiger");
		data.setProductNameTH("dfd");
		data.setIconId("123");
		fetchCardResponse.setProductCodeData(data);
		CreditCardEvent result = logService.loadCardDetailsEvent(creditCardEvent, hashMap, fetchCardResponse);
		assertEquals(creditCardEvent, result);
	}

	@Test
	public void testViewLoanLandingScreenEvent() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("correlationId", "activityDate", "activityTypeId");
		Map<String, String> hashMap = new HashMap();
		hashMap.put("1", "creditCard");
		hashMap.put("2", "debitCard");
		HomeLoanFullInfoResponse response = new HomeLoanFullInfoResponse();
		com.tmb.oneapp.productsexpservice.model.loan.StatusResponse status = new com.tmb.oneapp.productsexpservice.model.loan.StatusResponse();
		status.setCode("0");
		status.setDescription("Available");
		response.setStatus(status);
		Account accountId = new Account();
		response.setAccount(accountId);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setProductNameEN("Tiger");
		productConfig.setIconId("1234");
		response.setProductConfig(productConfig);
		response.getAccount().setId("0000000050078670143000945");
		CreditCardEvent result = logService.viewLoanLandingScreenEvent(creditCardEvent, hashMap, response);
		Assert.assertEquals(creditCardEvent, result);
	}

	@Test
	public void testLogActivity() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("correlationId", "activityDate", "activityTypeId");
		logService.logActivity(creditCardEvent);
		assertNotNull(creditCardEvent);
	}

	@Test
	public void testCallVerifyCardNoEvent() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("correlationId", "activityDate", "activityTypeId");
		Map<String, String> hashMap = new HashMap();
		hashMap.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078670143000945");
		hashMap.put("2", "debitCard");
		hashMap.put("3", "1234");
		creditCardEvent.setCardNumber(hashMap.get(ProductsExpServiceConstant.ACCOUNT_ID));
		creditCardEvent.setMethod(ProductsExpServiceConstant.METHOD);
		CreditCardEvent result = logService.callVerifyCardNoEvent(creditCardEvent, hashMap);
		Assert.assertEquals(creditCardEvent, result);
	}

	@Test
	public void testOnClickNextButtonEvent() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("correlationId", "activityDate", "activityTypeId");
		Map<String, String> hashMap = new HashMap();
		hashMap.put("1", "creditCard");
		hashMap.put("2", "debitCard");
		SetCreditLimitReq limitReq = new SetCreditLimitReq();
		limitReq.setRequestReason("1234");
		limitReq.setPreviousCreditLimit("12345");
		hashMap.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078670143000945");
		hashMap.put("2", "debitCard");
		hashMap.put("3", "1234");
		creditCardEvent.setCardNumber(hashMap.get(ProductsExpServiceConstant.ACCOUNT_ID));
		CreditCardEvent result = logService.onClickNextButtonEvent(creditCardEvent, hashMap, limitReq);
		Assert.assertEquals(creditCardEvent, result);
	}

	@Test
	public void testOnClickNextButtonLimitEvent() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("correlationId", "activityDate", "activityTypeId");
		Map<String, String> hashMap = new HashMap();
		hashMap.put("1", "creditCard");
		hashMap.put("2", "debitCard");
		SetCreditLimitReq limitReq = new SetCreditLimitReq();
		limitReq.setRequestReason("1234");
		limitReq.setPreviousCreditLimit("12345");
		CreditCardEvent result = logService.onClickNextButtonLimitEvent(creditCardEvent, hashMap, limitReq, "");
		Assert.assertEquals(creditCardEvent, result);
	}

	@Test
	public void testCompleteUsageListEvent() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("correlationId", "activityDate", "activityTypeId");
		Map<String, String> hashMap = new HashMap();
		hashMap.put("1", "creditCard");
		hashMap.put("2", "debitCard");
		SetCreditLimitReq limitReq = new SetCreditLimitReq();
		limitReq.setRequestReason("1234");
		limitReq.setPreviousCreditLimit("12345");
		limitReq.setAccountId("0000000050078670143000945");
		CreditCardEvent result = logService.completeUsageListEvent(creditCardEvent, hashMap, limitReq);
		Assert.assertEquals(creditCardEvent, result);
	}

	@Test
	public void testOnVerifyPinEvent() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("correlationId", "activityDate", "activityTypeId");
		Map<String, String> hashMap = new HashMap();
		hashMap.put("1", "creditCard");
		hashMap.put("2", "debitCard");
		hashMap.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078670143000945");
		creditCardEvent.setCardNumber(hashMap.get(ProductsExpServiceConstant.ACCOUNT_ID));
		creditCardEvent.setMethod(ProductsExpServiceConstant.METHOD);
		CreditCardEvent result = logService.onVerifyPinEvent(creditCardEvent, hashMap);
		Assert.assertEquals(creditCardEvent, result);
	}

	@Test
	void getCreditCardEvent() {
		CreditCardEvent creditCardEvent = new CreditCardEvent("correlationId", "activityDate", "activityTypeId");
		Map<String, String> hashMap = new HashMap();
		hashMap.put("1", "creditCard");
		hashMap.put("2", "debitCard");
		hashMap.put(ProductsExpServiceConstant.ACCOUNT_ID, "0000000050078670143000945");
		creditCardEvent.setCardNumber(hashMap.get(ProductsExpServiceConstant.ACCOUNT_ID));
		creditCardEvent.setMethod(ProductsExpServiceConstant.METHOD);
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		CardInstallmentQuery requestBody = getCardInstallmentQuery();
		List<CardInstallmentResponse> data = new ArrayList<>();
		for (CardInstallmentResponse response : data) {
			CreditCardModel creditCard = getCreditCardModel();
			response.setCreditCard(creditCard);
		}
		CardInstallment installment = getCardInstallment();
		CreditCardEvent result = logService.getCreditCardEvent(correlationId, hashMap, requestBody, data, installment);
		Assert.assertNotEquals(creditCardEvent, result);
	}

	private CreditCardModel getCreditCardModel() {
		CreditCardModel creditCard = new CreditCardModel();
		creditCard.setAccountId("0000000050078670143000945");
		CardInstallment card = new CardInstallment();
		card.setTransactionKey("1234");
		card.setTransactionDescription("Test");
		card.setTransactionKey("1234");
		card.setAmounts("1234.00");
		creditCard.setCardInstallment(card);
		return creditCard;
	}

	private CardInstallment getCardInstallment() {
		CardInstallment installment = new CardInstallment();
		installment.setTransactionKey("1234");
		installment.setAmounts("1234.00");
		installment.setInterest("1");
		installment.setModelType("test");
		installment.setInterest(ConversionUtil.doubleToString(10.00));
		installment.setPromotionModelNo("test");
		installment.setMonthlyInstallments(ConversionUtil.doubleToString(10.00));
		return installment;
	}

	private CardInstallmentQuery getCardInstallmentQuery() {
		CardInstallmentQuery requestBody = new CardInstallmentQuery();
		requestBody.setAccountId("0000000050078670143000945");
		List<CardInstallment> cardInstallment = new ArrayList<>();
		for (CardInstallment installment : cardInstallment) {
			installment.setInterest("1234");
			installment.setMonthlyInstallments(ConversionUtil.doubleToString(1234.00));
			installment.setInterest("1234.00");
			installment.setModelType("Test");
			installment.setPromotionModelNo("123");
			installment.setTransactionKey("test");
			cardInstallment.add(installment);
		}
		requestBody.setCardInstallment(cardInstallment);
		return requestBody;
	}

	@Test
	void testGetCardEvent() {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";

		HashMap<String, String> reqHeader = new HashMap<>() {
			{
				put("accept", "application/json");
			}
		};
		CardInstallmentQuery requestBody = getCardInstallmentQuery();
		CardInstallmentResponse cardInstallmentResponse = getCardInstallmentResponse();
		cardInstallmentResponse.getCreditCard().getCardInstallment().setTransactionKey("1234");
		cardInstallmentResponse.getCreditCard().getCardInstallment().setAmounts("1234.00");
		cardInstallmentResponse.getCreditCard().getCardInstallment().setTransactionDescription("Success");
		CardInstallment installment = getCardInstallment();
		logService.getCardEvent(correlationId, reqHeader, requestBody, Arrays.asList(cardInstallmentResponse),
				installment);
		assertNotNull(installment);
	}

	@Test
	void testCardResponse() {
		CardInstallment installment = getCardInstallment();
		logService.cardResponse(installment, creditCardEvent, getCardInstallmentResponse());
		assertNotNull(installment);
	}

	@Test
	void testSetFailEvent() {
		CardInstallmentResponse cardInstallmentResponse = getCardInstallmentResponse();
		creditCardEvent.setFailReason("fail");
		List<ErrorStatus> errorStatus = new ArrayList<>();
		ErrorStatus status = new ErrorStatus();
		status.setErrorCode("0001");
		status.setDescription("fail");
		errorStatus.add(status);
		cardInstallmentResponse.getStatus().setErrorStatus(errorStatus);
		logService.setFailEvent(creditCardEvent, cardInstallmentResponse);
		assertNotNull(cardInstallmentResponse);
	}
}
