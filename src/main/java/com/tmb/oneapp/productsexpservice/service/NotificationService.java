package com.tmb.oneapp.productsexpservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.NotificationConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.NotificationServiceClient;
import com.tmb.oneapp.productsexpservice.model.request.notification.EmailChannel;
import com.tmb.oneapp.productsexpservice.model.request.notification.NotificationRecord;
import com.tmb.oneapp.productsexpservice.model.request.notification.NotificationRequest;
import com.tmb.oneapp.productsexpservice.model.response.notification.NotificationResponse;

@Service
public class NotificationService {

	private static final TMBLogger<NotificationService> logger = new TMBLogger<>(NotificationService.class);

	private NotificationServiceClient notificationClient;
	private CustomerServiceClient customerClient;

	@Autowired
	public NotificationService(NotificationServiceClient notificationServiceClient,
			CustomerServiceClient customerServiceClient) {
		this.notificationClient = notificationServiceClient;
		this.customerClient = customerServiceClient;
	}

	public void sendActivationCardEmail(String email, String xCorrelationId, String channelNameEn, String channelNameTh,
			String accountId, String productNameEn, String productNameTh) {
		NotificationRequest notificationRequest = new NotificationRequest();
		List<NotificationRecord> notificationRecords = new ArrayList<NotificationRecord>();
		NotificationRecord emailRecord = new NotificationRecord();
		EmailChannel emailChannel = new EmailChannel();
		emailChannel.setEmailEndpoint(email);
		emailChannel.setEmailSearch(false);

		emailRecord.setEmail(emailChannel);

		Map<String, Object> emailTemplateParams = new HashMap<>();
		emailTemplateParams.put(NotificationConstant.ACTIVE_CARD_TEMPLATE_KEY,
				NotificationConstant.ACTIVE_CARD_TEMPLATE_VALUE);
		emailTemplateParams.put(NotificationConstant.ACTIVE_CARD_ACCOUNT_ID, accountId);
		emailTemplateParams.put(NotificationConstant.ACTIVE_CARD_CHANNEL_NAME_EN, channelNameEn);
		emailTemplateParams.put(NotificationConstant.ACTIVE_CARD_CHANNEL_NAME_TH, channelNameTh);
		emailTemplateParams.put(NotificationConstant.ACTIVE_CARD_PRODUCT_NAME_EN, productNameEn);
		emailTemplateParams.put(NotificationConstant.ACTIVE_CARD_PRODUCT_NAME_TH, productNameTh);
		emailRecord.setParams(emailTemplateParams);
		emailRecord.setLanguage("th");

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
