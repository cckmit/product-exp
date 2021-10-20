package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.ErrorStatusInfo;
import com.tmb.common.model.StatusResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.creditcard.CardInstallment;
import com.tmb.common.model.creditcard.CardInstallmentResponse;
import com.tmb.common.model.creditcard.CreditCardModel;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.NotificationServiceClient;
import com.tmb.oneapp.productsexpservice.model.SoGoodItemInfo;
import com.tmb.oneapp.productsexpservice.model.SoGoodWrapper;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.*;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CardInstallmentQuery;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.InstallmentPlan;
import com.tmb.common.model.request.notification.NotifyCommon;
import com.tmb.common.model.response.notification.NotificationResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class NotificationServiceTest {

	@Spy
	NotificationServiceClient notificationServiceClient;
	@Mock
	CustomerServiceClient customerServiceClient;
	@Mock
	CreditCardClient creditCardClient;
	@Mock
	CommonServiceClient commonServiceClient;
	@Mock
	TemplateService templateService;

	private NotificationService notificationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		notificationService = new NotificationService(notificationServiceClient, customerServiceClient,
				creditCardClient, commonServiceClient, templateService);
	}

	@Test
	void sendNotificationByEmailTriggerManual() {

		TmbOneServiceResponse<CustGeneralProfileResponse> profileResponse = new TmbOneServiceResponse<>();
		CustGeneralProfileResponse customerProfile = getCustomerProfileResponseData();
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("à¹‚à¸‹à¸Ÿà¸²à¸ªà¸•à¹Œ");
		cardResponse.setProductCodeData(productData);
		SilverlakeStatus silverlake = new SilverlakeStatus();
		silverlake.setStatusCode(0);
		cardResponse.setStatus(silverlake);
		CreditCardDetail cardDetail = new CreditCardDetail();
		cardDetail.setProductId("VTOPBR");
		cardResponse.setCreditCard(cardDetail);

		when(creditCardClient.getCreditCardDetails(any(), any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

		when(customerServiceClient.getCustomerProfile(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

		TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<>();
		List<ProductConfig> productConfigs = new ArrayList();
		productResponse.setData(productConfigs);
		when(commonServiceClient.getProductConfig(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);

		notificationService.sendCardActiveEmail(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208");
		Assert.assertTrue(true);
	}

	private CustGeneralProfileResponse getCustomerProfileResponseData() {
		CustGeneralProfileResponse customerProfile = new CustGeneralProfileResponse();
		customerProfile.setEmailAddress("witsanu.t@tcs.com");
		return customerProfile;
	}

	@Test
	void activeCardGetCustomerProfile() {
		TmbOneServiceResponse<CustGeneralProfileResponse> profileResponse = new TmbOneServiceResponse<>();
		CustGeneralProfileResponse customerProfile = getCustomerProfileResponseData();
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("à¹‚à¸‹à¸Ÿà¸²à¸ªà¸•à¹Œ");
		cardResponse.setProductCodeData(productData);
		SilverlakeStatus silverlake = new SilverlakeStatus();
		silverlake.setStatusCode(0);
		cardResponse.setStatus(silverlake);
		CreditCardDetail cardDetail = new CreditCardDetail();
		cardDetail.setProductId("VTOPBR");
		cardResponse.setCreditCard(cardDetail);

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);
		when(customerServiceClient.getCustomerProfile(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));
		when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID,
				"0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));
		TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<>();
		List<ProductConfig> productConfigs = new ArrayList<>();
		productResponse.setData(productConfigs);
		when(commonServiceClient.getProductConfig(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

		notificationService.sendCardActiveEmail(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208");
		Assert.assertTrue(true);

	}

	@Test
	void activeSetPinNotification() {
		TmbOneServiceResponse<CustGeneralProfileResponse> profileResponse = new TmbOneServiceResponse<>();
		CustGeneralProfileResponse customerProfile = new CustGeneralProfileResponse();
		customerProfile.setEmailAddress("witsanu@gmail.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("à¹‚à¸‹à¸Ÿà¸²à¸ªà¸•à¹Œ");
		cardResponse.setProductCodeData(productData);
		SilverlakeStatus silverlake = new SilverlakeStatus();
		silverlake.setStatusCode(0);
		cardResponse.setStatus(silverlake);
		CreditCardDetail cardDetail = new CreditCardDetail();
		cardDetail.setProductId("VTOPBR");
		cardResponse.setCreditCard(cardDetail);

		when(customerServiceClient.getCustomerProfile(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

		when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID,
				"0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));
		TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<>();
		List<ProductConfig> productConfigs = new ArrayList<>();
		productResponse.setData(productConfigs);
		when(commonServiceClient.getProductConfig(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);

		notificationService.doNotifySuccessForSetPin(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208");
		Assert.assertTrue(true);
	}

	@Test
	void activeBlockCardNotification() {
		TmbOneServiceResponse<CustGeneralProfileResponse> profileResponse = new TmbOneServiceResponse<>();
		CustGeneralProfileResponse customerProfile = new CustGeneralProfileResponse();
		customerProfile.setEmailAddress("witsanu@gmail.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("à¹‚à¸‹à¸Ÿà¸²à¸ªà¸•à¹Œ");
		cardResponse.setProductCodeData(productData);
		SilverlakeStatus silverlake = new SilverlakeStatus();
		silverlake.setStatusCode(0);
		cardResponse.setStatus(silverlake);

		CreditCardDetail cardDetail = new CreditCardDetail();
		cardDetail.setProductId("VTOPBR");
		cardResponse.setCreditCard(cardDetail);

		when(customerServiceClient.getCustomerProfile(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

		when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID,
				"0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

		TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<>();
		List<ProductConfig> productConfigs = new ArrayList<>();
		ProductConfig config = new ProductConfig();
		config.setProductCode(null);
		productResponse.setData(productConfigs);
		when(commonServiceClient.getProductConfig(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);

		notificationService.doNotifySuccessForBlockCard(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208");
		Assert.assertTrue(true);
	}

	@Test
	void changeTemporaryRequest() {
		SetCreditLimitReq req = new SetCreditLimitReq();
		req.setAccountId("0000000050079650011000193");
		req.setCurrentCreditLimit("120000");
		req.setEffectiveDate("2021-04-02");
		req.setReasonDesEn("For oversea emergency");
		req.setExpiryDate("1478-04-01T17:17:56.000Z");
		req.setMode("temporary");
		req.setPreviousCreditLimit("50000");
		req.setReasonDescEn("à¸�à¸£à¸“à¸µà¸‰à¸¸à¸�à¹€à¸‰à¸´à¸™à¹€à¸¡à¸·à¹ˆà¸­à¸­à¸¢à¸¹à¹ˆà¸•à¹ˆà¸²à¸‡à¸›à¸£à¸°à¹€à¸—à¸¨");
		req.setRequestReason("200");

		TmbOneServiceResponse<CustGeneralProfileResponse> profileResponse = new TmbOneServiceResponse<>();
		CustGeneralProfileResponse customerProfile = new CustGeneralProfileResponse();
		customerProfile.setEmailAddress("witsanu@gmail.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("à¹‚à¸‹à¸Ÿà¸²à¸ªà¸•à¹Œ");
		cardResponse.setProductCodeData(productData);
		SilverlakeStatus silverlake = new SilverlakeStatus();
		silverlake.setStatusCode(0);
		cardResponse.setStatus(silverlake);

		CreditCardDetail cardDetail = new CreditCardDetail();

		CardCreditLimit cardCreditLimit = new CardCreditLimit();
		cardCreditLimit.setPermanentCreditLimit(150000L);

		TemporaryCreditLimit tempCreditLimit = new TemporaryCreditLimit();
		tempCreditLimit.setAmounts(new BigDecimal("170000"));
		tempCreditLimit.setRequestReason("200");
		cardCreditLimit.setTemporaryCreditLimit(tempCreditLimit);

		cardDetail.setCardCreditLimit(cardCreditLimit);
		cardResponse.setCreditCard(cardDetail);

		when(customerServiceClient.getCustomerProfile(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

		when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID,
				"0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

		TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<>();
		List<ProductConfig> productConfigs = new ArrayList<ProductConfig>();
		productResponse.setData(productConfigs);
		when(commonServiceClient.getProductConfig(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);

		notificationService.doNotifySuccessForTemporaryLimit(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208", req);
		Assert.assertTrue(true);
	}

	@Test
	void changeUsageLimitTest() {
		SetCreditLimitReq req = new SetCreditLimitReq();
		req.setAccountId("0000000050079650011000193");
		req.setCurrentCreditLimit("120000");
		req.setEffectiveDate("2021-04-02");
		req.setReasonDesEn("For oversea emergency");
		req.setExpiryDate("1478-04-01T17:17:56.000Z");
		req.setMode("temporary");
		req.setPreviousCreditLimit("50000");
		req.setReasonDescEn("à¸�à¸£à¸“à¸µà¸‰à¸¸à¸�à¹€à¸‰à¸´à¸™à¹€à¸¡à¸·à¹ˆà¸­à¸­à¸¢à¸¹à¹ˆà¸•à¹ˆà¸²à¸‡à¸›à¸£à¸°à¹€à¸—à¸¨");
		req.setRequestReason("200");

		TmbOneServiceResponse<CustGeneralProfileResponse> profileResponse = new TmbOneServiceResponse<CustGeneralProfileResponse>();
		CustGeneralProfileResponse customerProfile = new CustGeneralProfileResponse();
		customerProfile.setEmailAddress("witsanu@gmail.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("à¹‚à¸‹à¸Ÿà¸²à¸ªà¸•à¹Œ");
		cardResponse.setProductCodeData(productData);
		SilverlakeStatus silverlake = new SilverlakeStatus();
		silverlake.setStatusCode(0);
		cardResponse.setStatus(silverlake);

		CreditCardDetail cardDetail = new CreditCardDetail();

		CardCreditLimit cardCreditLimit = new CardCreditLimit();
		cardCreditLimit.setPermanentCreditLimit(150000L);

		TemporaryCreditLimit tempCreditLimit = new TemporaryCreditLimit();
		tempCreditLimit.setAmounts(new BigDecimal("170000"));
		tempCreditLimit.setRequestReason("200");
		cardCreditLimit.setTemporaryCreditLimit(tempCreditLimit);

		cardDetail.setCardCreditLimit(cardCreditLimit);
		cardResponse.setCreditCard(cardDetail);

		when(customerServiceClient.getCustomerProfile(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

		when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID,
				"0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

		TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<List<ProductConfig>>();
		List<ProductConfig> productConfigs = new ArrayList<>();
		productResponse.setData(productConfigs);
		when(commonServiceClient.getProductConfig(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<NotificationResponse>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);
		notificationService.doNotifySuccessForChangeUsageLimit(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208", req);
		Assert.assertTrue(true);
	}

	@Test
	public void validCustomerResponseTest() {

		TmbOneServiceResponse<CustGeneralProfileResponse> profileResponse = new TmbOneServiceResponse<CustGeneralProfileResponse>();
		CustGeneralProfileResponse customerProfile = new CustGeneralProfileResponse();
		customerProfile.setEmailAddress("witsanu@gmail.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> ab = ResponseEntity.ok(profileResponse);
		notificationService.validCustomerResponse(ab);
		Assert.assertTrue(true);
	}

	@Test
	public void testDoNotifySuccessForBlockCard() {
		TmbOneServiceResponse<NotificationResponse> response = new TmbOneServiceResponse<>();
		TmbStatus status = new TmbStatus();
		status.setDescription("Successful");
		status.setCode("1234");
		status.setMessage("Successful");
		status.setService("notificationservice");
		response.setStatus(status);
		NotificationResponse data = new NotificationResponse();
		data.setStatus(0);
		data.setMessage("successful");
		data.setGuid("1234");
		data.setSuccess(true);
		response.setData(data);
		TmbOneServiceResponse<CustGeneralProfileResponse> profileResponse = new TmbOneServiceResponse<CustGeneralProfileResponse>();
		CustGeneralProfileResponse customerProfile = new CustGeneralProfileResponse();
		customerProfile.setEmailAddress("witsanu@gmail.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));
		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("à¹‚à¸‹à¸Ÿà¸²à¸ªà¸•à¹Œ");
		cardResponse.setProductCodeData(productData);
		SilverlakeStatus silverlake = new SilverlakeStatus();
		silverlake.setStatusCode(0);
		cardResponse.setStatus(silverlake);
		CreditCardDetail creditCard = new CreditCardDetail();
		creditCard.setAccountId("0000000050079650011000193");
		creditCard.setCardId("050079650011000193");
		creditCard.setDirectDepositBank("YES");
		cardResponse.setCreditCard(creditCard);
		cardResponse.setProductCodeData(productData);
		String accountId = "0000000050079650011000193";
		String correlationId = "1234";

		when(notificationServiceClient.sendMessage(anyString(), any())).thenReturn(response);

		when(customerServiceClient.getCustomerProfile(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

		when(creditCardClient.getCreditCardDetails(any(), any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

		TmbOneServiceResponse<List<ProductConfig>> productResponse = new TmbOneServiceResponse<>();
		List<ProductConfig> productConfigs = new ArrayList<>();
		productResponse.setData(productConfigs);
		when(commonServiceClient.getProductConfig(any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(productResponse));

		notificationService.doNotifySuccessForBlockCard(correlationId, accountId, "crmId");
		assertNotNull(data);
	}

	@Test
	public void testSendCardActiveEmail() {
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response = getTmbOneServiceResponseResponseEntity();
		notificationService.sendCardActiveEmail("xCorrelationId", "accountId", "crmId");
		assertNotNull(response);
	}

	@Test
	public void testValidCustomerResponse() {
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response = getTmbOneServiceResponseResponseEntity();
		boolean result = notificationService.validCustomerResponse(response);
		Assert.assertEquals(false, result);
	}

	@Test
	public void testDoNotifySuccessForSetPin() {
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response = getTmbOneServiceResponseResponseEntity();
		notificationService.doNotifySuccessForSetPin("xCorrelationId", "accountId", "crmId");
		assertNotNull(response);
	}

	private ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> getTmbOneServiceResponseResponseEntity() {
		TmbOneServiceResponse<CustGeneralProfileResponse> resp = getCustomerProfileResponseDataTmbOneServiceResponse();
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response = new ResponseEntity(resp,
				HttpStatus.OK);
		when(customerServiceClient.getCustomerProfile(any())).thenReturn(response);
		return response;
	}

	@Test
	public void testDoNotifySuccessForChangeUsageLimit() {
		SetCreditLimitReq requestBody = getSetCreditLimitReq();
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response = getTmbOneServiceResponseResponseEntity();
		notificationService.doNotifySuccessForChangeUsageLimit("xCorrelationId", "accountId", "crmId", requestBody);
		assertNotNull(requestBody);
	}

	@Test
	public void testDoNotifySuccessForTemporaryLimit() {
		SetCreditLimitReq requestBody = getSetCreditLimitReq();
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response = getTmbOneServiceResponseResponseEntity();
		notificationService.doNotifySuccessForTemporaryLimit("correlationId", "accountId", "crmId", requestBody);
		assertNotNull(requestBody);
	}

	private TmbOneServiceResponse<CustGeneralProfileResponse> getCustomerProfileResponseDataTmbOneServiceResponse() {
		Map<String, String> header = new HashMap<>();
		header.put("test", "test");
		TmbOneServiceResponse<CustGeneralProfileResponse> resp = new TmbOneServiceResponse<>();
		CustGeneralProfileResponse data = new CustGeneralProfileResponse();
		data.setEmailAddress("test@test.com");
		resp.setData(data);
		TmbStatus tmbStatus = new TmbStatus();
		tmbStatus.setService("notification-service");
		tmbStatus.setDescription("notification");
		tmbStatus.setCode("1234");
		tmbStatus.setMessage("1234");
		resp.setStatus(tmbStatus);
		return resp;
	}

	private SetCreditLimitReq getSetCreditLimitReq() {
		SetCreditLimitReq requestBody = new SetCreditLimitReq();
		requestBody.setAccountId("1234");
		requestBody.setRequestReason("test");
		requestBody.setCurrentCreditLimit("1243");
		requestBody.setMode("test");
		requestBody.setEffectiveDate("test");
		requestBody.setExpiryDate("test");
		requestBody.setPreviousCreditLimit("1234");
		requestBody.setReasonDescEn("test");
		requestBody.setPreviousCreditLimit("test");
		requestBody.setReasonDesTh("test");
		requestBody.setType("test");
		return requestBody;
	}

	@Test
	public void testDoNotifyApplySoGood() {
		CardInstallmentResponse response = getCardInstallmentResponse();
		CardInstallmentQuery requestBody = new CardInstallmentQuery();
		requestBody.setAccountId("1234");
		List<CardInstallment> cardInstallment = new ArrayList<>();
		CardInstallment element = new CardInstallment();
		InstallmentPlan installmentplan = getInstallmentPlan();
		List<InstallmentPlan> plan = new ArrayList<>();
		installmentplan.setInstallmentsPlan("1234");
		installmentplan.setChannel("1234");
		installmentplan.setPlanSeqId("1234");
		installmentplan.setInterestRate("1234");
		installmentplan.setPaymentTerm("1234");
		installmentplan.setPlanStatus("success");
		plan.add(installmentplan);
		requestBody.setCardInstallment(cardInstallment);
		TmbOneServiceResponse<List<InstallmentPlan>> tmbResponse = new TmbOneServiceResponse();
		CardInstallment cardInstall = new CardInstallment();
		cardInstall.setInterest("1234");

		TmbStatus tmbStatus = getTmbStatus();
		tmbResponse.setStatus(tmbStatus);
		tmbResponse.setData(plan);
		element.setPostDate("1234");

		cardInstallment.add(element);

		tmbResponse.setStatus(tmbStatus);
		tmbResponse.setData(plan);
		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("à¹‚à¸‹à¸Ÿà¸²à¸ªà¸•à¹Œ");
		cardResponse.setProductCodeData(productData);
		SilverlakeStatus silverlake = new SilverlakeStatus();
		silverlake.setStatusCode(0);
		cardResponse.setStatus(silverlake);
		CreditCardDetail cardDetail = new CreditCardDetail();
		cardDetail.setProductId("VTOPBR");
		cardResponse.setCreditCard(cardDetail);
		ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> responseInstallments = new ResponseEntity(
				tmbResponse, HttpStatus.OK);
		when(creditCardClient.getInstallmentPlan(any())).thenReturn(responseInstallments);
		when(creditCardClient.getCreditCardDetails(any(), any()))
		.thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

		notificationService.doNotifyApplySoGood("correlationId", "accountId", "crmId", Arrays.asList(response),
				requestBody);
		assertNotEquals(null, responseInstallments);
	}

	private CardInstallmentResponse getCardInstallmentResponse() {
		CardInstallmentResponse response = getInstallmentResponse();
		return response;
	}

	private CardInstallmentResponse getInstallmentResponse() {
		CardInstallmentResponse response = new CardInstallmentResponse();
		StatusResponse status = new StatusResponse();
		status.setStatusCode("0");
		ErrorStatusInfo errorStatus = new ErrorStatusInfo();
		errorStatus.setErrorCode("error code");
		errorStatus.setDescription("1234");

		List<ErrorStatusInfo> error = new ArrayList();
		for (ErrorStatusInfo stat : error) {
			stat.setDescription("error code");
			stat.setErrorCode("0");
			error.add(stat);
		}
		error.add(errorStatus);
		status.setErrorStatus(error);

		CreditCardModel model = new CreditCardModel();
		CardInstallment card = new CardInstallment();
		card.setAmounts("1234.00");
		card.setTransactionKey("1234");
		card.setTransactionDescription("success");
		model.setCardInstallment(card);
		model.setAccountId("124");
		response.setStatus(status);
		response.setCreditCard(model);
		return response;
	}

	private InstallmentPlan getInstallmentPlan() {
		InstallmentPlan installment = new InstallmentPlan();
		installment.setInstallmentsPlan("1234");
		installment.setChannel("1234");
		installment.setPlanSeqId("1234");
		installment.setInterestRate("1234");
		installment.setPaymentTerm("1234");
		installment.setPlanStatus("success");
		installment.setMerchantNo("1234");
		return installment;
	}

	private TmbStatus getTmbStatus() {
		TmbStatus tmbStatus = new TmbStatus();
		tmbStatus.setService("notification-service");
		tmbStatus.setDescription("notification");
		tmbStatus.setCode("1234");
		tmbStatus.setMessage("1234");
		return tmbStatus;
	}

	@Test
	void sendNotifyApplySoGood() {
		NotifyCommon notifyCommon = getNotifyCommon();
		String email = "test@test.com";
		String phoneNo = "9899776640";
		String productId = "VTOPBR";
		SoGoodWrapper soGoodWrapper = new SoGoodWrapper();
		List<SoGoodItemInfo> items = new ArrayList<>();
		SoGoodItemInfo soGoodItemInfo = new SoGoodItemInfo();
		soGoodItemInfo.setCreateDate("10-10-2020");
		soGoodItemInfo.setFirstPayment("1234");
		soGoodItemInfo.setName("test");
		items.add(soGoodItemInfo);
		soGoodWrapper.setItems(items);
		soGoodWrapper.setTenor("6");
		soGoodWrapper.setInterestRatePercent("10.00");
		BigDecimal totalAmt = BigDecimal.valueOf(100.00);
		TmbOneServiceResponse<NotificationResponse> response = new TmbOneServiceResponse<>();
		response.setStatus(getTmbStatus());
		NotificationResponse notification = new NotificationResponse();
		notification.setStatus(0);
		notification.setGuid("123");
		notification.setSuccess(true);
		notification.setMessage("Success");
		response.setData(notification);
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(response);
		notificationService.sendNotifyApplySoGood(notifyCommon, email, phoneNo, soGoodWrapper, totalAmt, productId);
		assertNotNull(response);
	}

	private NotifyCommon getNotifyCommon() {
		NotifyCommon notifyCommon = new NotifyCommon();
		notifyCommon.setCrmId("1234");
		notifyCommon.setAccountId("1234");
		notifyCommon.setProductNameEN("test");
		return notifyCommon;
	}

	@Test
	void generateSoGoodWraperModel() {
		InstallmentPlan installment = getInstallmentPlan();
		CardInstallmentResponse response = getCardInstallmentResponse();
		List<CardInstallmentResponse> successItems = new ArrayList<>();
		successItems.add(response);
		CardInstallmentQuery requestBody = new CardInstallmentQuery();
		requestBody.setAccountId("1234");
		List<CardInstallment> cardInstallment = new ArrayList<>();
		CardInstallment element = new CardInstallment();
		InstallmentPlan installmentplan = getInstallmentPlan();
		List<InstallmentPlan> plan = new ArrayList<>();
		installmentplan.setInstallmentsPlan("1234");
		installmentplan.setChannel("1234");
		installmentplan.setPlanSeqId("1234");
		installmentplan.setInterestRate("1234");
		installmentplan.setPaymentTerm("1234");
		installmentplan.setPlanStatus("success");
		plan.add(installmentplan);
		requestBody.setCardInstallment(cardInstallment);
		SoGoodWrapper soGoodWrapper = notificationService.generateSoGoodWraperModel(installment, successItems,
				requestBody);
		assertNotNull(soGoodWrapper);

	}

	@Test
	void testGetString() {
		String date = notificationService.getString("10-10-2020");
		assertNotNull(date);
	}

	@Test
	void testCardInstallmentData() {
		SoGoodItemInfo info = new SoGoodItemInfo();
		info.setName("test");
		CardInstallment value = new CardInstallment();
		value.setInterest("1234");
		Optional<CardInstallment> optCardInstallment = Optional.of(value);

		notificationService.cardInstallmentData(info, optCardInstallment);
		assertNotNull(optCardInstallment);
	}

	@Test
	void testProductCodeData() {
		ProductConfig productConfig = new ProductConfig();
		productConfig.setProductCode("test");
		ProductCodeData productCodeData = new ProductCodeData();
		productCodeData.setProductNameEN("test");
		notificationService.productCodeData(productConfig, productCodeData);
		assertNotNull(productConfig);
	}

}
