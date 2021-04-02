package com.tmb.oneapp.productsexpservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitReq;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.request.notification.EmailChannel;
import com.tmb.oneapp.productsexpservice.model.request.notification.NotificationRecord;
import com.tmb.oneapp.productsexpservice.model.request.notification.NotificationRequest;
import com.tmb.oneapp.productsexpservice.model.request.notification.NotifyCommon;
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

	private static final String FORMAT_TRAN_DATE = "dd/MM/yyyy";
	private static final String FORMAT_TIME = "HH:mm";

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
		logger.info("xCorrelationId:{} request customer name in th and en to card active", xCorrelationId);
		ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response = customerClient
				.getCustomerProfile(new HashMap<String, String>(), crmId);
		if (validCustomerResponse(response)) {
			CustomerProfileResponseData customerProfileInfo = response.getBody().getData();

			ResponseEntity<FetchCardResponse> cardInfoResponse = creditCardClient.getCreditCardDetails(xCorrelationId,
					accountId);
			if (Objects.nonNull(cardInfoResponse.getBody())
					&& SILVER_LAKE_SUCCESS_CODE.equals(cardInfoResponse.getBody().getStatus().getStatusCode())) {
				FetchCardResponse cardResponse = cardInfoResponse.getBody();
				sendActivationCardEmail(customerProfileInfo.getEmailAddress(), xCorrelationId, defaultChannelEn,
						defaultChannelTh, accountId, cardResponse.getProductCodeData().getProductNameEN(),
						cardResponse.getProductCodeData().getProductNameTH());
			}
		}
	}

	/**
	 * Validae for sucess customer response
	 * 
	 * @param response
	 * @return
	 */
	public boolean validCustomerResponse(ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response) {
		return HttpStatus.OK == response.getStatusCode() && Objects.nonNull(response.getBody())
				&& Objects.nonNull(response.getBody().getData())
				&& ResponseCode.SUCESS.getCode().equals(response.getBody().getStatus().getCode());
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

			Map<String, Object> params = new HashMap<>();
			params.put(NotificationConstant.TEMPLATE_KEY, NotificationConstant.ACTIVE_CARD_TEMPLATE_VALUE);
			params.put(NotificationConstant.ACCOUNT_ID, accountId);
			params.put(NotificationConstant.CHANNEL_NAME_EN, channelNameEn);
			params.put(NotificationConstant.CHANNEL_NAME_TH, channelNameTh);
			params.put(NotificationConstant.PRODUCT_NAME_EN, productNameEn);
			params.put(NotificationConstant.PRODUCT_NAME_TH, productNameTh);
			emailRecord.setParams(params);
			emailRecord.setLanguage(NotificationConstant.LOCALE_TH);

			notificationRecords.add(emailRecord);
			notificationRequest.setRecords(notificationRecords);

			TmbOneServiceResponse<NotificationResponse> sendEmailResponse = notificationClient
					.sendMessage(xCorrelationId, notificationRequest);

			processResultLog(sendEmailResponse, notificationRequest);

		}

	}

	/**
	 * Wrapper execution for notify success for set pin for email and sms
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
		if (validCustomerResponse(response)) {
			CustomerProfileResponseData customerProfileInfo = response.getBody().getData();

			ResponseEntity<FetchCardResponse> cardInfoResponse = creditCardClient.getCreditCardDetails(xCorrelationId,
					accountId);
			if (Objects.nonNull(cardInfoResponse.getBody())
					&& SILVER_LAKE_SUCCESS_CODE.equals(cardInfoResponse.getBody().getStatus().getStatusCode())) {
				FetchCardResponse cardResponse = cardInfoResponse.getBody();
				NotifyCommon notifyCommon = new NotifyCommon();
				notifyCommon.setChannelNameEn(defaultChannelEn);
				notifyCommon.setChannelNameTh(defaultChannelTh);
				notifyCommon.setEmail(customerProfileInfo.getEmailAddress());
				notifyCommon.setSmsNo(customerProfileInfo.getPhoneNoFull());
				sendNotificationEmailForSetpin(notifyCommon, xCorrelationId, accountId,
						cardResponse.getProductCodeData().getProductNameEN(),
						cardResponse.getProductCodeData().getProductNameTH(), gobalCallCenter);
			}
		}
	}

	/**
	 * Wrapper for process notification for SET PIN
	 * 
	 * @param notifyCommon
	 * @param xCorrelationId
	 * @param accountId
	 * @param productNameEn
	 * @param productNameTh
	 * @param supportNo
	 */
	private void sendNotificationEmailForSetpin(NotifyCommon notifyCommon, String xCorrelationId, String accountId,
			String productNameEn, String productNameTh, String supportNo) {
		NotificationRequest notificationRequest = new NotificationRequest();
		List<NotificationRecord> notificationRecords = new ArrayList<>();

		NotificationRecord record = new NotificationRecord();

		Map<String, Object> params = new HashMap<>();
		params.put(NotificationConstant.TEMPLATE_KEY, NotificationConstant.SET_PIN_TEMPLATE_VALUE);
		params.put(NotificationConstant.ACCOUNT_ID, accountId);
		params.put(NotificationConstant.CHANNEL_NAME_EN, notifyCommon.getChannelNameEn());
		params.put(NotificationConstant.CHANNEL_NAME_TH, notifyCommon.getChannelNameTh());
		params.put(NotificationConstant.PRODUCT_NAME_EN, productNameEn);
		params.put(NotificationConstant.PRODUCT_NAME_TH, productNameTh);
		params.put(NotificationConstant.SUPPORT_NO, supportNo);
		record.setParams(params);
		record.setLanguage(NotificationConstant.LOCALE_TH);

		setRequestForEmailAndSms(notifyCommon, record);

		notificationRecords.add(record);
		notificationRequest.setRecords(notificationRecords);
		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = notificationClient.sendMessage(xCorrelationId,
				notificationRequest);
		processResultLog(sendEmailResponse, notificationRequest);
	}

	/**
	 * Method for notify change usage email service for wrapper process
	 * 
	 * @param xCorrelationId
	 * @param accountId
	 * @param crmId
	 * @param requestBodyParameter
	 */
	@Async
	public void doNotifySuccessForChangeUsageLimit(String xCorrelationId, String accountId, String crmId,
			SetCreditLimitReq requestBodyParameter) {
		logger.info("xCorrelationId:{} request customer name in th and en for change usage limit", xCorrelationId);
		ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response = customerClient
				.getCustomerProfile(new HashMap<String, String>(), crmId);
		if (validCustomerResponse(response)) {
			CustomerProfileResponseData customerProfileInfo = response.getBody().getData();

			ResponseEntity<FetchCardResponse> cardInfoResponse = creditCardClient.getCreditCardDetails(xCorrelationId,
					accountId);
			if (Objects.nonNull(cardInfoResponse.getBody())
					&& SILVER_LAKE_SUCCESS_CODE.equals(cardInfoResponse.getBody().getStatus().getStatusCode())) {
				FetchCardResponse cardResponse = cardInfoResponse.getBody();
				NotifyCommon notifyCommon = new NotifyCommon();
				notifyCommon.setAccountId(accountId);
				notifyCommon.setEmail(customerProfileInfo.getEmailAddress());
				notifyCommon.setProductNameEN(cardResponse.getProductCodeData().getProductNameEN());
				notifyCommon.setProductNameTH(cardResponse.getProductCodeData().getProductNameTH());
				notifyCommon.setXCorrelationId(xCorrelationId);

				notifyCommon
						.setCustFullNameEn(customerProfileInfo.getEngFname() + " " + customerProfileInfo.getEngLname());
				notifyCommon
						.setCustFullNameTH(customerProfileInfo.getThaFname() + " " + customerProfileInfo.getThaLname());

				String tranDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMAT_TRAN_DATE));
				String tranTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMAT_TIME));
				sendNotifySuccessForChangeUsage(notifyCommon, requestBodyParameter.getPreviousCreditLimit(),
						requestBodyParameter.getCurrentCreditLimit(), tranDate, tranTime);

			}
		}
	}

	/**
	 * Wrapper for process notification for CHANGE USAGE
	 * 
	 * @param notifyCommon
	 * @param oldLimit
	 * @param newLimit
	 * @param tranDate
	 * @param tranTime
	 */
	private void sendNotifySuccessForChangeUsage(NotifyCommon notifyCommon, String oldLimit, String newLimit,
			String tranDate, String tranTime) {

		NotificationRequest notificationRequest = new NotificationRequest();
		List<NotificationRecord> notificationRecords = new ArrayList<>();
		NotificationRecord record = new NotificationRecord();

		Map<String, Object> params = new HashMap<>();
		params.put(NotificationConstant.TEMPLATE_KEY, NotificationConstant.CHANGE_USAGE_TEMPLATE_VALUE);
		params.put(NotificationConstant.CUSTOMER_NAME_EN, notifyCommon.getCustFullNameEn());
		params.put(NotificationConstant.CUSTOMER_NAME_TH, notifyCommon.getCustFullNameTH());
		params.put(NotificationConstant.PRODUCT_NAME_EN, notifyCommon.getProductNameEN());
		params.put(NotificationConstant.PRODUCT_NAME_TH, notifyCommon.getProductNameTH());
		params.put(NotificationConstant.ACCOUNT_ID, notifyCommon.getAccountId());
		params.put(NotificationConstant.OLD_CREDIT_LIMIT, oldLimit);
		params.put(NotificationConstant.NEW_CREDIT_LIMIT, newLimit);
		params.put(NotificationConstant.TRAN_DATE, tranDate);
		params.put(NotificationConstant.TRAN_TIME, tranTime);

		record.setParams(params);

		setRequestForEmailAndSms(notifyCommon, record);

		notificationRecords.add(record);
		notificationRequest.setRecords(notificationRecords);

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = notificationClient
				.sendMessage(notifyCommon.getXCorrelationId(), notificationRequest);

		processResultLog(sendEmailResponse, notificationRequest);

	}

	/**
	 * set param for email and sms
	 * 
	 * @param notifyCommon
	 * @param record
	 */
	private void setRequestForEmailAndSms(NotifyCommon notifyCommon, NotificationRecord record) {
		// case email
		if (StringUtils.isNotBlank(notifyCommon.getEmail())) {
			EmailChannel emailChannel = new EmailChannel();
			emailChannel.setEmailEndpoint(notifyCommon.getEmail());
			emailChannel.setEmailSearch(false);

			record.setEmail(emailChannel);

		}
		// case sms
		if (StringUtils.isNotBlank(notifyCommon.getSmsNo())) {
			SmsChannel smsChannel = new SmsChannel();
			smsChannel.setSmsEdpoint(notifyCommon.getSmsNo());
			smsChannel.setSmsSearch(false);
			smsChannel.setSmsForce(false);
			record.setSms(smsChannel);
		}
	}

	/**
	 * Method for notify request temporary limit email service for wrapper process
	 * 
	 * @param correlationId
	 * @param accountId
	 * @param crmId
	 * @param requestBodyParameter
	 */
	@Async
	public void doNotifySuccessForTemporaryLimit(String correlationId, String accountId, String crmId,
			SetCreditLimitReq requestBodyParameter) {
		logger.info("xCorrelationId:{} request customer name in th and en to temporary limit", correlationId);
		ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response = customerClient
				.getCustomerProfile(new HashMap<String, String>(), crmId);
		if (validCustomerResponse(response)) {
			CustomerProfileResponseData customerProfileInfo = response.getBody().getData();

			ResponseEntity<FetchCardResponse> cardInfoResponse = creditCardClient.getCreditCardDetails(correlationId,
					accountId);
			if (Objects.nonNull(cardInfoResponse.getBody())
					&& SILVER_LAKE_SUCCESS_CODE.equals(cardInfoResponse.getBody().getStatus().getStatusCode())) {
				FetchCardResponse cardResponse = cardInfoResponse.getBody();
				NotifyCommon notifyCommon = new NotifyCommon();
				notifyCommon.setAccountId(accountId);
				notifyCommon.setEmail(customerProfileInfo.getEmailAddress());
				notifyCommon.setProductNameEN(cardResponse.getProductCodeData().getProductNameEN());
				notifyCommon.setProductNameTH(cardResponse.getProductCodeData().getProductNameTH());
				notifyCommon.setXCorrelationId(correlationId);
				notifyCommon
						.setCustFullNameEn(customerProfileInfo.getEngFname() + " " + customerProfileInfo.getEngLname());
				notifyCommon
						.setChannelNameTh(customerProfileInfo.getThaFname() + " " + customerProfileInfo.getThaLname());
				String expiryDate = cardResponse.getCreditCard().getCardCreditLimit().getTemporaryCreditLimit()
						.getExpiryDate();
				String tempLimit = cardResponse.getCreditCard().getCardCreditLimit().getTemporaryCreditLimit()
						.getAmounts() != null
								? cardResponse.getCreditCard().getCardCreditLimit().getTemporaryCreditLimit()
										.getAmounts().toString()
								: null;
				String reasonEN = requestBodyParameter.getEnglishDes();
				String reasonTH = requestBodyParameter.getReasonDescEn();
				sendNotifySuccessForRequestTemporary(notifyCommon, expiryDate, tempLimit, reasonEN, reasonTH);

			}
		}
	}

	/**
	 * 
	 * @param correlationId
	 * @param accountId
	 * @param crmId
	 */
	@Async
	public void doNotifySuccessForBlockCard(String correlationId, String accountId, String crmId) {
		logger.info("xCorrelationId:{} request customer name in th and en for block card", correlationId);
		ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> response = customerClient
				.getCustomerProfile(new HashMap<String, String>(), crmId);
		if (validCustomerResponse(response)) {
			CustomerProfileResponseData customerProfileInfo = response.getBody().getData();

			ResponseEntity<FetchCardResponse> cardInfoResponse = creditCardClient.getCreditCardDetails(correlationId,
					accountId);
			if (Objects.nonNull(cardInfoResponse.getBody())
					&& SILVER_LAKE_SUCCESS_CODE.equals(cardInfoResponse.getBody().getStatus().getStatusCode())) {
				FetchCardResponse cardResponse = cardInfoResponse.getBody();
				NotifyCommon notifyCommon = new NotifyCommon();
				notifyCommon.setChannelNameEn(defaultChannelEn);
				notifyCommon.setChannelNameTh(defaultChannelTh);
				notifyCommon.setEmail(customerProfileInfo.getEmailAddress());
				notifyCommon.setSmsNo(customerProfileInfo.getPhoneNoFull());
				notifyCommon.setProductNameEN(cardResponse.getProductCodeData().getProductNameEN());
				notifyCommon.setProductNameTH(cardResponse.getProductCodeData().getProductNameTH());
				notifyCommon.setXCorrelationId(correlationId);
				notifyCommon.setAccountId(accountId);
				sendNotificationEmailForBlockCard(notifyCommon, gobalCallCenter);
			}
		}

	}

	/**
	 * Wrapper for process notification for Block card
	 * 
	 * @param notifyCommon
	 * @param gobalCallCenter
	 */
	private void sendNotificationEmailForBlockCard(NotifyCommon notifyCommon, String gobalCallCenter) {
		NotificationRequest notificationRequest = new NotificationRequest();
		List<NotificationRecord> notificationRecords = new ArrayList<>();

		NotificationRecord record = new NotificationRecord();

		Map<String, Object> params = new HashMap<>();
		params.put(NotificationConstant.TEMPLATE_KEY, NotificationConstant.BLOCK_CARD_TEMPLATE_VALUE);
		params.put(NotificationConstant.ACCOUNT_ID, notifyCommon.getAccountId());
		params.put(NotificationConstant.CHANNEL_NAME_EN, notifyCommon.getChannelNameEn());
		params.put(NotificationConstant.CHANNEL_NAME_TH, notifyCommon.getChannelNameTh());
		params.put(NotificationConstant.PRODUCT_NAME_EN, notifyCommon.getProductNameEN());
		params.put(NotificationConstant.PRODUCT_NAME_TH, notifyCommon.getProductNameTH());
		params.put(NotificationConstant.SUPPORT_NO, gobalCallCenter);
		record.setParams(params);
		record.setLanguage(NotificationConstant.LOCALE_TH);

		setRequestForEmailAndSms(notifyCommon, record);

		notificationRecords.add(record);
		notificationRequest.setRecords(notificationRecords);
		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = notificationClient
				.sendMessage(notifyCommon.getXCorrelationId(), notificationRequest);

		processResultLog(sendEmailResponse, notificationRequest);

	}

	/**
	 * Wrapper for process notification for Request temporary
	 * 
	 * @param notifyCommon
	 * @param expiryDate
	 * @param tempLimit
	 * @param reasonEN
	 * @param reasonTH
	 */
	private void sendNotifySuccessForRequestTemporary(NotifyCommon notifyCommon, String expiryDate, String tempLimit,
			String reasonEN, String reasonTH) {

		NotificationRequest notificationRequest = new NotificationRequest();
		List<NotificationRecord> notificationRecords = new ArrayList<>();
		NotificationRecord record = new NotificationRecord();

		Map<String, Object> params = new HashMap<>();
		params.put(NotificationConstant.TEMPLATE_KEY, NotificationConstant.REQUEST_TEMPORARY_TEMPLATE_VALUE);
		params.put(NotificationConstant.CUSTOMER_NAME_EN, notifyCommon.getCustFullNameEn());
		params.put(NotificationConstant.CUSTOMER_NAME_TH, notifyCommon.getCustFullNameTH());
		params.put(NotificationConstant.PRODUCT_NAME_EN, notifyCommon.getProductNameEN());
		params.put(NotificationConstant.PRODUCT_NAME_TH, notifyCommon.getProductNameTH());
		params.put(NotificationConstant.ACCOUNT_ID, notifyCommon.getAccountId());
		params.put(NotificationConstant.TEMP_LIMIT, tempLimit);
		params.put(NotificationConstant.EXPIRE_DATE, expiryDate);
		params.put(NotificationConstant.REASON_EN, reasonEN);
		params.put(NotificationConstant.REASON_TH, reasonTH);

		record.setParams(params);

		setRequestForEmailAndSms(notifyCommon, record);

		notificationRecords.add(record);
		notificationRequest.setRecords(notificationRecords);

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = notificationClient
				.sendMessage(notifyCommon.getXCorrelationId(), notificationRequest);

		processResultLog(sendEmailResponse, notificationRequest);

	}

	/**
	 * Log response for e-notification system
	 * 
	 * @param sendEmailResponse
	 * @param notificationRequest
	 */
	private void processResultLog(TmbOneServiceResponse<NotificationResponse> sendEmailResponse,
			NotificationRequest notificationRequest) {
		if (ResponseCode.SUCESS.getCode().equals(sendEmailResponse.getStatus().getCode())) {
			logger.info("xCorrelationId:{} ,e-noti response sent email success", notificationRequest);
		} else {
			logger.error("xCorrelationId:{}, e-noti response sent email error:{}, {}", notificationRequest,
					sendEmailResponse.getStatus().getCode(), sendEmailResponse.getStatus().getMessage());
		}
	}

}