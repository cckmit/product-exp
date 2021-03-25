package com.tmb.oneapp.productsexpservice.service;

import org.junit.Ignore;
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

import com.tmb.common.model.CustomerProfileResponseData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.NotificationServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.GetCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductCodeData;
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
	@Ignore
	void sendNotificationByEmailTriggerManual() {
		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<NotificationResponse>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);
		notificationService.sendActivationCardEmail("witsanu.t@tsc.com",
				ProductsExpServiceConstant.HEADER_CORRELATION_ID, "TMB Touch", "ทีเอ็มบี ทัซ",
				"0000000050079650011000193", "Product Test En", "ผลิตภัณฑ์ 1");
	}

	@Test
	void activeCardGetCustomerProfile() {
		TmbOneServiceResponse<CustomerProfileResponseData> profileResponse = new TmbOneServiceResponse<CustomerProfileResponseData>();
		CustomerProfileResponseData customerProfile = new CustomerProfileResponseData();
		customerProfile.setEmailAddress("witsanu.t@tcs.com");
		profileResponse.setData(customerProfile);
		profileResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "customer-service"));

		GetCardResponse cardResponse = new GetCardResponse();
		ProductCodeData productData = new ProductCodeData();
		productData.setProductNameEN("So Fast Credit Card");
		productData.setProductNameTH("โซฟาสต์");
		cardResponse.setProductCodeData(productData);

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = new TmbOneServiceResponse<NotificationResponse>();
		sendEmailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "succcess", "notification-service"));
		when(notificationServiceClient.sendMessage(any(), any())).thenReturn(sendEmailResponse);
		when(customerServiceClient.getCustomerProfile(any(), any()))
				.thenReturn(ResponseEntity.status(HttpStatus.OK).body(profileResponse));
		when(creditCardClient.getCreditCardDetails(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardResponse));
		
		notificationService.sendCardActiveEmail(ProductsExpServiceConstant.HEADER_CORRELATION_ID,
				"0000000050079650011000193", "001100000000000000000012036208");

	}

}
