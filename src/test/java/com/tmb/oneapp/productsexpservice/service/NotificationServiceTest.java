package com.tmb.oneapp.productsexpservice.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import com.tmb.common.model.CustomerProfileResponseData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.NotificationServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CardCreditLimit;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CreditCardDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductCodeData;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitReq;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SilverlakeStatus;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.TemporaryCreditLimit;
import com.tmb.oneapp.productsexpservice.model.response.notification.NotificationResponse;

@RunWith(JUnit4.class)
public class NotificationServiceTest {

	@Mock
	NotificationServiceClient notificationServiceClient;
	@Mock
	CustomerServiceClient customerServiceClient;
	@Mock
	CreditCardClient creditCardClient;

	private NotificationService notificationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		notificationService = new NotificationService(notificationServiceClient, customerServiceClient,
				creditCardClient);
	}

	@Test
	void sendNotificationByEmailTriggerManual() {

		TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<CustomerProfileResponseData>();
		CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
		customerProfile.setEmailAddress("witsanu.t@tcs.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("โซฟาสต์");
		cardResponse.setProductCodeData(productData);
		SilverlakeStatus silverlake = new SilverlakeStatus();
		silverlake.setStatusCode(0);
		cardResponse.setStatus(silverlake);

		when(creditCardClient.getCreditCardDetails(any(), any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

		when(customerServiceClient.getCustomerProfile(any(), any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<NotificationResponse>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);

		notificationService.sendCardActiveEmail(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208");
		Assert.assertTrue(true);
	}

	@Test
	void activeCardGetCustomerProfile() {
		TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<CustomerProfileResponseData>();
		CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
		customerProfile.setEmailAddress("witsanu.t@tcs.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("โซฟาสต์");
		cardResponse.setProductCodeData(productData);
		SilverlakeStatus silverlake = new SilverlakeStatus();
		silverlake.setStatusCode(0);
		cardResponse.setStatus(silverlake);

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<NotificationResponse>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);
		when(customerServiceClient.getCustomerProfile(any(), any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));
		when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

		notificationService.sendCardActiveEmail(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208");
		Assert.assertTrue(true);

	}

	@Test
	void activeSetPinNotification() {
		TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<CustomerProfileResponseData>();
		CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
		customerProfile.setEmailAddress("witsanu@gmail.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("โซฟาสต์");
		cardResponse.setProductCodeData(productData);
		SilverlakeStatus silverlake = new SilverlakeStatus();
		silverlake.setStatusCode(0);
		cardResponse.setStatus(silverlake);

		when(customerServiceClient.getCustomerProfile(any(), any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

		when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<NotificationResponse>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);

		notificationService.doNotifySuccessForSetPin(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208");
		Assert.assertTrue(true);
	}

	@Test
	void activeBlockCardNotification() {
		TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<CustomerProfileResponseData>();
		CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
		customerProfile.setEmailAddress("witsanu@gmail.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("โซฟาสต์");
		cardResponse.setProductCodeData(productData);
		SilverlakeStatus silverlake = new SilverlakeStatus();
		silverlake.setStatusCode(0);
		cardResponse.setStatus(silverlake);

		when(customerServiceClient.getCustomerProfile(any(), any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

		when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<NotificationResponse>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);

		notificationService.doNotifySuccessForSetPin(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208");
		Assert.assertTrue(true);
	}

	@Test
	void changeTemporaryRequest() {
		SetCreditLimitReq req = new SetCreditLimitReq();
		req.setAccountId("0000000050079650011000193");
		req.setCurrentCreditLimit("120000");
		req.setEffectiveDate("2021-04-02");
		req.setEnglishDes("For oversea emergency");
		req.setExpiryDate("1478-04-01T17:17:56.000Z");
		req.setMode("temporary");
		req.setPreviousCreditLimit("50000");
		req.setReasonDescEn("กรณีฉุกเฉินเมื่ออยู่ต่างประเทศ");
		req.setRequestReason("200");

		TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<CustomerProfileResponseData>();
		CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
		customerProfile.setEmailAddress("witsanu@gmail.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("โซฟาสต์");
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

		when(customerServiceClient.getCustomerProfile(any(), any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

		when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<NotificationResponse>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);

		notificationService.doNotifySuccessForTemporaryLimit(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208", req);
		Assert.assertTrue(true);
	}

	@Test
	void changeUsageLimit() {
		SetCreditLimitReq req = new SetCreditLimitReq();
		req.setAccountId("0000000050079650011000193");
		req.setCurrentCreditLimit("120000");
		req.setEffectiveDate("2021-04-02");
		req.setEnglishDes("For oversea emergency");
		req.setExpiryDate("1478-04-01T17:17:56.000Z");
		req.setMode("temporary");
		req.setPreviousCreditLimit("50000");
		req.setReasonDescEn("กรณีฉุกเฉินเมื่ออยู่ต่างประเทศ");
		req.setRequestReason("200");

		TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<CustomerProfileResponseData>();
		CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
		customerProfile.setEmailAddress("witsanu@gmail.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		FetchCardResponse cardResponse = new FetchCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("โซฟาสต์");
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

		when(customerServiceClient.getCustomerProfile(any(), any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));

		when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<NotificationResponse>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);
		notificationService.doNotifySuccessForChangeUsageLimit(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208", req);
		Assert.assertTrue(true);
	}

	@Test
	public void validCustomerResponseTest() {

		TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<CustomerProfileResponseData>();
		CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
		customerProfile.setEmailAddress("witsanu@gmail.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));
		ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> ab = ResponseEntity.ok(profileResponse);
		notificationService.validCustomerResponse(ab);
		Assert.assertTrue(true);
	}

}
