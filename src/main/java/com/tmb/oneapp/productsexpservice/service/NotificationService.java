package com.tmb.oneapp.productsexpservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CustomerProfileResponseData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.NotificationConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.NotificationServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.GetCardResponse;
import com.tmb.oneapp.productsexpservice.model.request.notification.EmailChannel;
import com.tmb.oneapp.productsexpservice.model.request.notification.NotificationRecord;
import com.tmb.oneapp.productsexpservice.model.request.notification.NotificationRequest;
import com.tmb.oneapp.productsexpservice.model.request.notification.SmsChannel;
import com.tmb.oneapp.productsexpservice.model.response.notification.NotificationResponse;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

@Service
public class NotificationService {

	private static final TMBLogger<NotificationService> logger = new TMBLogger<>(NotificationService.class);

	@Value("${notification-service.e-noti.default.channel.th}")
	private String defaultChannelTh;
	@Value("${notification-service.e-noti.default.channel.en}")
	private String defaultChannelEn;
	@Value("${notification-service.e-noti.default.support.no}")
	private String gobalCallCenter;

	private NotificationServiceClient notificationClient;
	private CustomerServiceClient customerClient;
	private CreditCardClient creditCardClient;

	@Autowired
	public NotificationService(NotificationServiceClient notificationServiceClient,
			CustomerServiceClient customerServiceClient, CreditCardClient creditCardClient) {
		this.notificationClient = notificationServiceClient;
		this.customerClient = customerServiceClient;
		this.creditCardClient = creditCardClient;
	}

	/**
	 * Method for activation email service for expose to external request
	 * 
	 * @param xCorrelationId
	 * @param accountId
	 * @param crmId
	 */
	@Async
	public void sendCardActiveEmail(String xCorrelationId, String accountId, String crmId) {
		logger.info("xCorrelationId:{} request customer name in th and en to customer-service", xCorrelationId);
		ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response = customerClient
				.getCustomerProfile(new HashMap<String, String>(), crmId);
		if (HttpStatus.OK == response.getStatusCode() && Objects.nonNull(response.getBody())
				&& Objects.nonNull(response.getBody().getData())
				&& ResponseCode.SUCESS.getCode().equals(response.getBody().getStatus().getCode())) {
			CustomerProfileResponseData customerProfileInfo = response.getBody().getData();

			ResponseEntity<GetCardResponse> cardInfoResponse = creditCardClient.getCreditCardDetails(xCorrelationId,
					accountId);
			if (Objects.nonNull(cardInfoResponse.getBody())
					&& SILVER_LAKE_SUCCESS_CODE.equals(cardInfoResponse.getBody().getStatus().getStatusCode())) {
				GetCardResponse cardResponse = cardInfoResponse.getBody();
				sendActivationCardEmail(customerProfileInfo.getEmailAddress(), xCorrelationId, defaultChannelEn,
						defaultChannelTh, accountId, cardResponse.getProductCodeData().getProductNameEN(),
						cardResponse.getProductCodeData().getProductNameTH());
			}
		}
	}

	/**
	 * Method for activation email service for wrapper process
	 * 
	 * @param email
	 * @param xCorrelationId
	 * @param channelNameEn
	 * @param channelNameTh
	 * @param accountId
	 * @param productNameEn
	 * @param productNameTh
	 */
	private void sendActivationCardEmail(String email, String xCorrelationId, String channelNameEn,
			String channelNameTh, String accountId, String productNameEn, String productNameTh) {

		if (StringUtils.isNotBlank(email)) {
			NotificationRequest notificationRequest = new NotificationRequest();
			List<NotificationRecord> notificationRecords = new ArrayList<>();
			NotificationRecord emailRecord = new NotificationRecord();
			EmailChannel emailChannel = new EmailChannel();
			emailChannel.setEmailEndpoint(email);
			emailChannel.setEmailSearch(false);

			emailRecord.setEmail(emailChannel);

			Map<String, Object> emailTemplateParams = new HashMap<>();
			emailTemplateParams.put(NotificationConstant.EMAIL_TEMPLATE_KEY,
					NotificationConstant.ACTIVE_CARD_TEMPLATE_VALUE);
			emailTemplateParams.put(NotificationConstant.EMAIL_CARD_ACCOUNT_ID, accountId);
			emailTemplateParams.put(NotificationConstant.EMAIL_CHANNEL_NAME_EN, channelNameEn);
			emailTemplateParams.put(NotificationConstant.EMAIL_CHANNEL_NAME_TH, channelNameTh);
			emailTemplateParams.put(NotificationConstant.EMAIL_PRODUCT_NAME_EN, productNameEn);
			emailTemplateParams.put(NotificationConstant.EMAIL_PRODUCT_NAME_TH, productNameTh);
			emailRecord.setParams(emailTemplateParams);
			emailRecord.setLanguage(NotificationConstant.LOCALE_TH);

			notificationRecords.add(emailRecord);
			notificationRequest.setRecords(notificationRecords);

			TmbOneServiceResponse<NotificationResponse> sendEmailResponse = notificationClient
					.sendMessage(xCorrelationId, notificationRequest);
			if (ResponseCode.SUCESS.getCode().equals(sendEmailResponse.getStatus().getCode())) {
				logger.info("xCorrelationId:{} ,e-noti response sent email success", notificationRequest);
			} else {
				logger.error("xCorrelationId:{}, e-noti response sent email error:{}, {}", notificationRequest,
						sendEmailResponse.getStatus().getCode(), sendEmailResponse.getStatus().getMessage());
			}
		}

	}

	/**
	 * Wrapper execution for notify success for set pin  for email and sms
	 * 
	 * @param xCorrelationId
	 * @param accountId
	 * @param crmId
	 */
	@Async
	public void doNotifySuccessForSetPin(String xCorrelationId, String accountId, String crmId) {
		logger.info("xCorrelationId:{} request customer name in th and en to customer-service", xCorrelationId);
		ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response = customerClient
				.getCustomerProfile(new HashMap<String, String>(), crmId);
		if (HttpStatus.OK == response.getStatusCode() && Objects.nonNull(response.getBody())
				&& Objects.nonNull(response.getBody().getData())
				&& ResponseCode.SUCESS.getCode().equals(response.getBody().getStatus().getCode())) {
			CustomerProfileResponseData customerProfileInfo = response.getBody().getData();

			ResponseEntity<GetCardResponse> cardInfoResponse = creditCardClient.getCreditCardDetails(xCorrelationId,
					accountId);
			if (Objects.nonNull(cardInfoResponse.getBody())
					&& SILVER_LAKE_SUCCESS_CODE.equals(cardInfoResponse.getBody().getStatus().getStatusCode())) {
				GetCardResponse cardResponse = cardInfoResponse.getBody();
				sendNotificationEmailForSetpin(customerProfileInfo.getPhoneNoFull(),
						customerProfileInfo.getEmailAddress(), xCorrelationId, defaultChannelEn, defaultChannelTh,
						accountId, cardResponse.getProductCodeData().getProductNameEN(),
						cardResponse.getProductCodeData().getProductNameTH(), gobalCallCenter);
			}
		}
	}
	/**
	 * Wrapper for process notification for SET PIN
	 * @param smsNo
	 * @param email
	 * @param xCorrelationId
	 * @param channelNameEn
	 * @param channelNameTh
	 * @param accountId
	 * @param productNameEn
	 * @param productNameTh
	 * @param supportNo
	 */
	private void sendNotificationEmailForSetpin(String smsNo, String email, String xCorrelationId, String channelNameEn,
			String channelNameTh, String accountId, String productNameEn, String productNameTh, String supportNo) {
		NotificationRequest notificationRequest = new NotificationRequest();
		List<NotificationRecord> notificationRecords = new ArrayList<>();
		NotificationRecord record = new NotificationRecord();

		// case email
		if (StringUtils.isNotBlank(email)) {
			EmailChannel emailChannel = new EmailChannel();
			emailChannel.setEmailEndpoint(email);
			emailChannel.setEmailSearch(false);

			record.setEmail(emailChannel);

			Map<String, Object> emailTemplateParams = new HashMap<>();
			emailTemplateParams.put(NotificationConstant.EMAIL_TEMPLATE_KEY,
					NotificationConstant.SET_PIN_TEMPLATE_VALUE);
			emailTemplateParams.put(NotificationConstant.EMAIL_CARD_ACCOUNT_ID, accountId);
			emailTemplateParams.put(NotificationConstant.EMAIL_CHANNEL_NAME_EN, channelNameEn);
			emailTemplateParams.put(NotificationConstant.EMAIL_CHANNEL_NAME_TH, channelNameTh);
			emailTemplateParams.put(NotificationConstant.EMAIL_PRODUCT_NAME_EN, productNameEn);
			emailTemplateParams.put(NotificationConstant.EMAIL_PRODUCT_NAME_TH, productNameTh);
			emailTemplateParams.put(NotificationConstant.EMAIL_SUPPORT_NO, supportNo);
			record.setParams(emailTemplateParams);
			record.setLanguage(NotificationConstant.LOCALE_TH);

			notificationRecords.add(record);
		}
		
		// case sms
		if (StringUtils.isNotBlank(smsNo)) {
			SmsChannel smsChannel = new SmsChannel();
			smsChannel.setSmsEdpoint(smsNo);
			smsChannel.setSmsSearch(false);
			smsChannel.setSmsForce(false);
			record.setSms(smsChannel);
		}

		notificationRequest.setRecords(notificationRecords);
		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = notificationClient.sendMessage(xCorrelationId,
				notificationRequest);
		if (ResponseCode.SUCESS.getCode().equals(sendEmailResponse.getStatus().getCode())) {
			logger.info("xCorrelationId:{} ,e-noti response sent email success", notificationRequest);
		} else {
			logger.error("xCorrelationId:{}, e-noti response sent email error:{}, {}", notificationRequest,
					sendEmailResponse.getStatus().getCode(), sendEmailResponse.getStatus().getMessage());
		}

	}

}
