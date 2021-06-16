package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.creditcard.CardInstallment;
import com.tmb.oneapp.productsexpservice.constant.NotificationConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.NotificationServiceClient;
import com.tmb.oneapp.productsexpservice.model.MonthlyTrans;
import com.tmb.oneapp.productsexpservice.model.SoGoodItemInfo;
import com.tmb.oneapp.productsexpservice.model.SoGoodWrapper;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductCodeData;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitReq;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CardInstallmentQuery;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CardInstallmentResponse;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.InstallmentPlan;
import com.tmb.oneapp.productsexpservice.model.request.notification.*;
import com.tmb.oneapp.productsexpservice.model.response.notification.NotificationResponse;
import com.tmb.oneapp.productsexpservice.util.NotificationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.SILVER_LAKE_SUCCESS_CODE;

@Service
public class NotificationService {

	private static final TMBLogger<NotificationService> logger = new TMBLogger<>(NotificationService.class);

	@Value("${notification-service.e-noti.default.channel.th}")
	private String defaultChannelTh;
	@Value("${notification-service.e-noti.default.channel.en}")
	private String defaultChannelEn;
	@Value("${notification-service.e-noti.default.support.no}")
	private String gobalCallCenter;
	@Value("${notification-service.e-noti.default.template.date}")

	private static final String HTML_DATE_FORMAT = "dd/MM/yyyy";
	@Value("${notification-service.e-noti.default.template.time}")
	private static final String HH_MM = "HH:mm";

	private final NotificationServiceClient notificationClient;
	private final CustomerServiceClient customerClient;
	private final CreditCardClient creditCardClient;
	private final CommonServiceClient commonServiceClient;
	private final TemplateService templateService;

	@Autowired
	public NotificationService(NotificationServiceClient notificationServiceClient,
			CustomerServiceClient customerServiceClient, CreditCardClient creditCardClient,
			CommonServiceClient commonServiceClient, TemplateService templateService) {

		this.notificationClient = notificationServiceClient;
		this.customerClient = customerServiceClient;
		this.creditCardClient = creditCardClient;
		this.commonServiceClient = commonServiceClient;
		this.templateService = templateService;
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
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response = customerClient
				.getCustomerProfile(crmId);
		if (validCustomerResponse(response)) {
			CustGeneralProfileResponse customerProfileInfo = response.getBody().getData();

			ResponseEntity<FetchCardResponse> cardInfoResponse = creditCardClient.getCreditCardDetails(xCorrelationId,
					accountId);
			ProductCodeData productCodeData = generateProductCodeData(cardInfoResponse, xCorrelationId);
			if (Objects.nonNull(cardInfoResponse.getBody())
					&& SILVER_LAKE_SUCCESS_CODE.equals(cardInfoResponse.getBody().getStatus().getStatusCode())) {
				NotifyCommon notifyCommon = NotificationUtil.generateNotifyCommon(xCorrelationId, defaultChannelEn,
						defaultChannelTh, productCodeData.getProductNameEN(), productCodeData.getProductNameTH(),
						customerProfileInfo.getEngFname() + " " + customerProfileInfo.getEngLname(),
						customerProfileInfo.getThaFname() + " " + customerProfileInfo.getThaLname());
				notifyCommon.setAccountId(accountId);
				notifyCommon.setCrmId(crmId);
				sendActivationCardEmail(notifyCommon, customerProfileInfo.getEmailAddress());
			}
		}
	}

	/**
	 * Validae for sucess customer response
	 *
	 * @param response
	 * @return
	 */
	public boolean validCustomerResponse(ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response) {
		return HttpStatus.OK == response.getStatusCode() && Objects.nonNull(response.getBody())
				&& Objects.nonNull(response.getBody().getData())
				&& ResponseCode.SUCESS.getCode().equals(response.getBody().getStatus().getCode());
	}

	/**
	 * Method for activation email service for wrapper process
	 *
	 * @param notifyCommon
	 * @param email
	 */
	private void sendActivationCardEmail(NotifyCommon notifyCommon, String email) {

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
			params.put(NotificationConstant.ACCOUNT_ID, notifyCommon.getAccountId());
			params.put(NotificationConstant.CUSTOMER_NAME_EN, notifyCommon.getCustFullNameEn());
			params.put(NotificationConstant.CUSTOMER_NAME_TH, notifyCommon.getCustFullNameTH());
			params.put(NotificationConstant.CHANNEL_NAME_EN, notifyCommon.getChannelNameEn());
			params.put(NotificationConstant.CHANNEL_NAME_TH, notifyCommon.getChannelNameTh());
			params.put(NotificationConstant.PRODUCT_NAME_EN, notifyCommon.getProductNameEN());
			params.put(NotificationConstant.PRODUCT_NAME_TH, notifyCommon.getProductNameTH());
			emailRecord.setParams(params);
			emailRecord.setLanguage(NotificationConstant.LOCALE_TH);
			emailRecord.setCrmId(notifyCommon.getCrmId());

			notificationRecords.add(emailRecord);

			notificationRequest.setRecords(notificationRecords);

			TmbOneServiceResponse<NotificationResponse> sendEmailResponse = notificationClient
					.sendMessage(notifyCommon.getXCorrelationId(), notificationRequest);

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
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response = customerClient
				.getCustomerProfile(crmId);
		if (validCustomerResponse(response)) {
			CustGeneralProfileResponse customerProfileInfo = response.getBody().getData();

			ResponseEntity<FetchCardResponse> cardInfoResponse = creditCardClient.getCreditCardDetails(xCorrelationId,
					accountId);
			if (Objects.nonNull(cardInfoResponse.getBody())
					&& SILVER_LAKE_SUCCESS_CODE.equals(cardInfoResponse.getBody().getStatus().getStatusCode())) {

				ProductCodeData productCodeData = generateProductCodeData(cardInfoResponse, xCorrelationId);
				NotifyCommon notifyCommon = NotificationUtil.generateNotifyCommon(xCorrelationId, defaultChannelEn,
						defaultChannelTh, productCodeData.getProductNameEN(), productCodeData.getProductNameTH(),
						customerProfileInfo.getEngFname() + " " + customerProfileInfo.getEngLname(),
						customerProfileInfo.getThaFname() + " " + customerProfileInfo.getThaLname());
				notifyCommon.setAccountId(accountId);
				notifyCommon.setCrmId(crmId);

				sendNotificationEmailForSetpin(notifyCommon, gobalCallCenter, customerProfileInfo.getEmailAddress(),
						customerProfileInfo.getPhoneNoFull());
			}
		}
	}

	/**
	 * Wrapper for process notification for SET PIN
	 *
	 * @param notifyCommon
	 * @param supportNo
	 */
	private void sendNotificationEmailForSetpin(NotifyCommon notifyCommon, String supportNo, String email,
			String smsNo) {
		NotificationRequest notificationRequest = new NotificationRequest();
		List<NotificationRecord> notificationRecords = new ArrayList<>();

		NotificationRecord record = new NotificationRecord();

		Map<String, Object> params = new HashMap<>();
		params.put(NotificationConstant.TEMPLATE_KEY, NotificationConstant.SET_PIN_TEMPLATE_VALUE);
		params.put(NotificationConstant.ACCOUNT_ID, notifyCommon.getAccountId());
		params.put(NotificationConstant.CUSTOMER_NAME_EN, notifyCommon.getCustFullNameEn());
		params.put(NotificationConstant.CUSTOMER_NAME_TH, notifyCommon.getCustFullNameTH());
		params.put(NotificationConstant.CHANNEL_NAME_EN, notifyCommon.getChannelNameEn());
		params.put(NotificationConstant.CHANNEL_NAME_TH, notifyCommon.getChannelNameTh());
		params.put(NotificationConstant.PRODUCT_NAME_EN, notifyCommon.getProductNameEN());
		params.put(NotificationConstant.PRODUCT_NAME_TH, notifyCommon.getProductNameTH());
		params.put(NotificationConstant.SUPPORT_NO, supportNo);
		record.setParams(params);
		record.setLanguage(NotificationConstant.LOCALE_TH);
		record.setCrmId(notifyCommon.getCrmId());

		setRequestForEmailAndSms(email, smsNo, record);

		notificationRecords.add(record);
		notificationRequest.setRecords(notificationRecords);
		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = notificationClient
				.sendMessage(notifyCommon.getXCorrelationId(), notificationRequest);
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
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response = customerClient
				.getCustomerProfile(crmId);

		if (validCustomerResponse(response)) {
			CustGeneralProfileResponse customerProfileInfo = response.getBody().getData();

			ResponseEntity<FetchCardResponse> cardInfoResponse = creditCardClient.getCreditCardDetails(xCorrelationId,
					accountId);
			if (Objects.nonNull(cardInfoResponse.getBody())
					&& SILVER_LAKE_SUCCESS_CODE.equals(cardInfoResponse.getBody().getStatus().getStatusCode())) {
				ProductCodeData productCodeData = generateProductCodeData(cardInfoResponse, xCorrelationId);
				String fullNameEng = customerProfileInfo.getEngFname() + " " + customerProfileInfo.getEngLname();
				String fullNameThai = customerProfileInfo.getThaFname() + " " + customerProfileInfo.getThaLname();
				NotifyCommon notifyCommon = NotificationUtil.generateNotifyCommon(xCorrelationId, defaultChannelEn,
						defaultChannelTh, productCodeData.getProductNameEN(), productCodeData.getProductNameTH(),
						fullNameEng, fullNameThai);
				notifyCommon.setAccountId(accountId);
				notifyCommon.setCrmId(crmId);

				String tranDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern(HTML_DATE_FORMAT));
				String tranTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(HH_MM));
				sendNotifySuccessForChangeUsage(notifyCommon,
						formateForCurrency(requestBodyParameter.getPreviousCreditLimit()),
						formateForCurrency(requestBodyParameter.getCurrentCreditLimit()),
						customerProfileInfo.getEmailAddress(), tranDate, tranTime);

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
	 * @param email
	 */
	private void sendNotifySuccessForChangeUsage(NotifyCommon notifyCommon, String oldLimit, String newLimit,
			String email, String tranDate, String tranTime) {

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
		record.setCrmId(notifyCommon.getCrmId());
		record.setLanguage(NotificationConstant.LOCALE_TH);

		setRequestForEmailAndSms(email, null, record);

		notificationRecords.add(record);
		notificationRequest.setRecords(notificationRecords);

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = notificationClient
				.sendMessage(notifyCommon.getXCorrelationId(), notificationRequest);

		processResultLog(sendEmailResponse, notificationRequest);

	}

	/**
	 * set param for email and sms
	 *
	 * @param record
	 */
	private void setRequestForEmailAndSms(String email, String smsNo, NotificationRecord record) {
		// case email
		if (StringUtils.isNotBlank(email)) {
			EmailChannel emailChannel = new EmailChannel();
			emailChannel.setEmailEndpoint(email);
			emailChannel.setEmailSearch(false);

			record.setEmail(emailChannel);

		}
		// case sms
		if (StringUtils.isNotBlank(smsNo)) {
			SmsChannel smsChannel = new SmsChannel();
			smsChannel.setSmsEdpoint(smsNo);
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
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response = customerClient
				.getCustomerProfile(crmId);
		if (validCustomerResponse(response)) {
			CustGeneralProfileResponse customerProfileInfo = response.getBody().getData();

			ResponseEntity<FetchCardResponse> cardInfoResponse = creditCardClient.getCreditCardDetails(correlationId,
					accountId);
			if (Objects.nonNull(cardInfoResponse.getBody())
					&& SILVER_LAKE_SUCCESS_CODE.equals(cardInfoResponse.getBody().getStatus().getStatusCode())) {
				FetchCardResponse cardResponse = cardInfoResponse.getBody();
				ProductCodeData productCodeData = generateProductCodeData(cardInfoResponse, correlationId);
				NotifyCommon notifyCommon = NotificationUtil.generateNotifyCommon(correlationId, defaultChannelEn,
						defaultChannelTh, productCodeData.getProductNameEN(), productCodeData.getProductNameTH(),
						customerProfileInfo.getEngFname() + " " + customerProfileInfo.getEngLname(),
						customerProfileInfo.getThaFname() + " " + customerProfileInfo.getThaLname());
				notifyCommon.setAccountId(accountId);
				notifyCommon.setCrmId(crmId);

				String expiryDate = formateDateWithStandard(
						cardResponse.getCreditCard().getCardCreditLimit().getTemporaryCreditLimit().getExpiryDate());

				String tempLimit = formateForCurrency(
						cardResponse.getCreditCard().getCardCreditLimit().getTemporaryCreditLimit().getAmounts() != null
								? cardResponse.getCreditCard().getCardCreditLimit().getTemporaryCreditLimit()
										.getAmounts().toString()
								: null);
				String reasonEN = requestBodyParameter.getReasonDescEn();
				String reasonTH = requestBodyParameter.getReasonDesTh();
				sendNotifySuccessForRequestTemporary(notifyCommon, customerProfileInfo.getEmailAddress(), expiryDate,
						tempLimit, reasonEN, reasonTH);

			}
		}
	}

	/**
	 * 
	 * @param moneyString
	 * @return
	 */
	private String formateForCurrency(String moneyString) {

		try {
			BigDecimal money = new BigDecimal(moneyString);
			return String.format("%,.2f", money);
		} catch (Exception e) {
			logger.error("Invalid money input " + moneyString);
		}

		return null;
	}

	/**
	 * 
	 * @param money
	 * @return
	 */
	private String formateForCurrency(BigDecimal money) {
		return String.format("%,.2f", money);
	}

	/**
	 * Conversion rate
	 *
	 * @param expiryDate
	 * @return
	 */
	private String formateDateWithStandard(String expiryDate) {
		if (StringUtils.isEmpty(expiryDate)) {
			return null;
		}
		return getString(expiryDate);
	}

	/**
	 * @param expiryDate
	 * @return
	 */
	String getString(String expiryDate) {
		String sourcePattern = "yyyy-MM-dd";

		SimpleDateFormat sourceDateFormat = new SimpleDateFormat(sourcePattern);
		SimpleDateFormat targetDateFormat = new SimpleDateFormat(HTML_DATE_FORMAT);
		String htmlDate = null;
		try {
			Date sourceDate = sourceDateFormat.parse(expiryDate);
			htmlDate = targetDateFormat.format(sourceDate);
		} catch (ParseException e) {
			logger.error(e.toString(), e);
		}
		return htmlDate;
	}

	/**
	 * expose service for block card
	 *
	 * @param correlationId
	 * @param accountId
	 * @param crmId
	 */
	@Async
	public void doNotifySuccessForBlockCard(String correlationId, String accountId, String crmId) {
		logger.info("xCorrelationId:{} request customer name in th and en for block card", correlationId);
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response = customerClient
				.getCustomerProfile(crmId);
		if (validCustomerResponse(response)) {
			CustGeneralProfileResponse customerProfileInfo = response.getBody().getData();

			ResponseEntity<FetchCardResponse> cardInfoResponse = creditCardClient.getCreditCardDetails(correlationId,
					accountId);
			if (Objects.nonNull(cardInfoResponse.getBody())
					&& SILVER_LAKE_SUCCESS_CODE.equals(cardInfoResponse.getBody().getStatus().getStatusCode())) {
				ProductCodeData productCodeData = generateProductCodeData(cardInfoResponse, correlationId);
				NotifyCommon notifyCommon = NotificationUtil.generateNotifyCommon(correlationId, defaultChannelEn,
						defaultChannelTh, productCodeData.getProductNameEN(), productCodeData.getProductNameTH(),
						customerProfileInfo.getEngFname() + " " + customerProfileInfo.getEngLname(),
						customerProfileInfo.getThaFname() + " " + customerProfileInfo.getThaLname());
				notifyCommon.setAccountId(accountId);
				notifyCommon.setCrmId(crmId);

				sendNotificationEmailForBlockCard(notifyCommon, customerProfileInfo.getEmailAddress(),
						customerProfileInfo.getPhoneNoFull(), gobalCallCenter);
			}
		}

	}

	/**
	 * Wrapper for process notification for Block card
	 *
	 * @param notifyCommon
	 * @param gobalCallCenter
	 * @param smsNo
	 * @param email
	 */
	private void sendNotificationEmailForBlockCard(NotifyCommon notifyCommon, String email, String smsNo,
			String gobalCallCenter) {
		NotificationRequest notificationRequest = new NotificationRequest();
		List<NotificationRecord> notificationRecords = new ArrayList<>();

		NotificationRecord record = new NotificationRecord();
		record.setCrmId(notifyCommon.getCrmId());

		Map<String, Object> params = new HashMap<>();
		params.put(NotificationConstant.TEMPLATE_KEY, NotificationConstant.BLOCK_CARD_TEMPLATE_VALUE);
		params.put(NotificationConstant.ACCOUNT_ID, notifyCommon.getAccountId());
		params.put(NotificationConstant.CUSTOMER_NAME_EN, notifyCommon.getCustFullNameEn());
		params.put(NotificationConstant.CUSTOMER_NAME_TH, notifyCommon.getCustFullNameTH());
		params.put(NotificationConstant.CHANNEL_NAME_EN, notifyCommon.getChannelNameEn());
		params.put(NotificationConstant.CHANNEL_NAME_TH, notifyCommon.getChannelNameTh());
		params.put(NotificationConstant.PRODUCT_NAME_EN, notifyCommon.getProductNameEN());
		params.put(NotificationConstant.PRODUCT_NAME_TH, notifyCommon.getProductNameTH());
		params.put(NotificationConstant.SUPPORT_NO, gobalCallCenter);
		record.setParams(params);
		record.setLanguage(NotificationConstant.LOCALE_TH);

		setRequestForEmailAndSms(email, smsNo, record);

		notificationRecords.add(record);
		notificationRequest.setRecords(notificationRecords);
		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = notificationClient
				.sendMessage(notifyCommon.getXCorrelationId(), notificationRequest);

		processResultLog(sendEmailResponse, notificationRequest);

	}

	/**
	 * Wrapper for process request temporary
	 *
	 * @param notifyCommon
	 * @param email
	 * @param expiryDate
	 * @param tempLimit
	 * @param reasonEN
	 * @param reasonTH
	 */
	private void sendNotifySuccessForRequestTemporary(NotifyCommon notifyCommon, String email, String expiryDate,
			String tempLimit, String reasonEN, String reasonTH) {

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
		record.setCrmId(notifyCommon.getCrmId());
		record.setLanguage(NotificationConstant.LOCALE_TH);

		setRequestForEmailAndSms(email, null, record);

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

	/**
	 * Method for fetch Product Code Data model from card response
	 *
	 * @param cardInfoResponse
	 * @param correlationId
	 * @return
	 */
	private ProductCodeData generateProductCodeData(ResponseEntity<FetchCardResponse> cardInfoResponse,
			String correlationId) {
		ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> response = commonServiceClient
				.getProductConfig(correlationId);
		List<ProductConfig> productConfigList = response.getBody().getData();
		String productCode = cardInfoResponse.getBody().getCreditCard().getProductId();
		ProductConfig productConfig = productConfigList.stream().filter(e -> productCode.equals(e.getProductCode()))
				.findAny().orElse(null);
		ProductCodeData productCodeData = new ProductCodeData();
		if (Objects.nonNull(productConfig)) {
			productCodeData(productConfig, productCodeData);
		}
		return productCodeData;
	}

	void productCodeData(ProductConfig productConfig, ProductCodeData productCodeData) {
		productCodeData.setProductNameTH(productConfig.getProductNameTH());
		productCodeData.setProductNameEN(productConfig.getProductNameEN());
		productCodeData.setIconId(productConfig.getIconId());
	}

	/**
	 * Expose for notify apply so good
	 *
	 * @param correlationId
	 * @param accountId
	 * @param crmId
	 * @param data
	 * @param requestBodyParameter
	 */
	@Async
	public void doNotifyApplySoGood(String correlationId, String accountId, String crmId,
			List<CardInstallmentResponse> data, CardInstallmentQuery requestBodyParameter) {
		logger.info("xCorrelationId:{} request apply SO Good", correlationId);

		List<CardInstallmentResponse> successItems = fillerForSuccessCardInstallmentRequest(data);
		if (CollectionUtils.isNotEmpty(successItems)) {
			InstallmentPlan installment = lookUpInstallment(correlationId,
					requestBodyParameter.getCardInstallment().get(0).getPromotionModelNo());
			BigDecimal totalAmt = calculateTotalSoGoodAmt(successItems);
			ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> response = customerClient
					.getCustomerProfile(crmId);
			if (Objects.nonNull(installment) && Objects.nonNull(response.getBody())) {
				CustGeneralProfileResponse profileResponseData = response.getBody().getData();
				NotifyCommon notifyCommon = NotificationUtil.generateNotifyCommon(correlationId, defaultChannelEn,
						defaultChannelTh, null, null,
						profileResponseData.getEngFname() + " " + profileResponseData.getEngLname(),
						profileResponseData.getThaFname() + " " + profileResponseData.getThaLname());
				notifyCommon.setAccountId(accountId);
				notifyCommon.setCrmId(crmId);
				SoGoodWrapper soGoodWrapperInfo = generateSoGoodWraperModel(installment, successItems,
						requestBodyParameter);
				sendNotifyApplySoGood(notifyCommon, profileResponseData.getEmailAddress(),
						profileResponseData.getPhoneNoFull(), soGoodWrapperInfo, totalAmt);
			}

		}
	}

	/**
	 * Process generate model for so good warpper model
	 *
	 * @param installment
	 * @param successItems
	 * @param requestBodyParameter
	 * @return
	 */
	SoGoodWrapper generateSoGoodWraperModel(InstallmentPlan installment, List<CardInstallmentResponse> successItems,
			CardInstallmentQuery requestBodyParameter) {
		SoGoodWrapper wrapperInfo = new SoGoodWrapper();
		wrapperInfo.setTenor(installment.getPaymentTerm());
		wrapperInfo.setInterestRatePercent(installment.getInterestRate());

		List<SoGoodItemInfo> itemInfos = new ArrayList<>();
		successItems.forEach(item -> {
			Double amount = Double.parseDouble(item.getCreditCard().getCardInstallment().getAmounts());
			MonthlyTrans monthlyTrans = InstallmentService.calcualteMonthlyTransection(new BigDecimal(amount),
					Integer.parseInt(installment.getPaymentTerm()), new BigDecimal(installment.getInterestRate()));
			SoGoodItemInfo info = new SoGoodItemInfo();
			Optional<CardInstallment> optCardInstallment = requestBodyParameter.getCardInstallment().stream()
					.filter(e -> e.getTransactionKey()
							.equals(item.getCreditCard().getCardInstallment().getTransactionKey()))
					.collect(Collectors.toList()).stream().findFirst();
			if (optCardInstallment.isPresent()) {
				cardInstallmentData(info, optCardInstallment);
			}

			info.setFirstPayment(formateForCurrency(monthlyTrans.getFirstPayment()));
			info.setName(item.getCreditCard().getCardInstallment().getTransactionDescription());
			info.setPrinciple(formateForCurrency(new BigDecimal(amount)));
			info.setTotalAmt(formateForCurrency(monthlyTrans.getTotalAmt()));
			info.setTotalInterest(formateForCurrency(monthlyTrans.getTotalInterest()));
			itemInfos.add(info);
		});
		wrapperInfo.setItems(itemInfos);
		return wrapperInfo;
	}

	void cardInstallmentData(SoGoodItemInfo info, Optional<CardInstallment> optCardInstallment) {
		if (optCardInstallment.isPresent()) {
			CardInstallment cardInstallment = optCardInstallment.get();
			info.setCreateDate(formateDateWithStandard(cardInstallment.getPostDate()));
			info.setTranDate(formateDateWithStandard(cardInstallment.getTransectionDate()));
		}
	}

	/**
	 * Calculate total So Good Amount
	 *
	 * @param successItems
	 * @return
	 */
	BigDecimal calculateTotalSoGoodAmt(List<CardInstallmentResponse> successItems) {
		BigDecimal totalAmt = BigDecimal.ZERO;
		for (CardInstallmentResponse installment : successItems) {
			Double amount = Double.parseDouble(installment.getCreditCard().getCardInstallment().getAmounts());
			totalAmt = totalAmt.add(new BigDecimal(amount));
		}
		return totalAmt;
	}

	/**
	 * Find out select InstallmentPlan
	 *
	 * @param correlationId
	 * @param promotionModelNo
	 * @return
	 */
	InstallmentPlan lookUpInstallment(String correlationId, String promotionModelNo) {
		ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> responseInstallments = creditCardClient
				.getInstallmentPlan(correlationId);
		List<InstallmentPlan> installmentPlans = responseInstallments.getBody().getData();
		InstallmentPlan reqInstallmentPlan = null;
		for (InstallmentPlan installmentplan : installmentPlans) {
			if (installmentplan.getInstallmentsPlan().equals(promotionModelNo)) {
				reqInstallmentPlan = installmentplan;
			}
		}
		return reqInstallmentPlan;
	}

	/**
	 * fillter for succes installment request
	 *
	 * @param data
	 * @return
	 */
	List<CardInstallmentResponse> fillerForSuccessCardInstallmentRequest(List<CardInstallmentResponse> data) {
		List<CardInstallmentResponse> successCardInstallments = new ArrayList<>();
		data.forEach(e -> {
			if (e.getStatus().getStatusCode().equals("0")) {
				successCardInstallments.add(e);
			}
		});
		return successCardInstallments;
	}

	/**
	 * Wrapper for apply so good
	 *
	 * @param notifyCommon
	 * @param email
	 * @param phoneNo
	 * @param SoGoodWrapper
	 */
	void sendNotifyApplySoGood(NotifyCommon notifyCommon, String email, String phoneNo, SoGoodWrapper soGoodWrapper,
			BigDecimal totalAmt) {
		NotificationRequest notificationRequest = new NotificationRequest();
		List<NotificationRecord> notificationRecords = new ArrayList<>();
		NotificationRecord record = new NotificationRecord();

		String term = soGoodWrapper.getTenor();
		String soGoodTotalFormatedAmt = formateForCurrency(totalAmt);

		Context ctx = new Context();
		ctx.setVariable("items", soGoodWrapper.getItems());

		String totalDesTh = templateService.getSoGoodItemTh(ctx);
		String totalDesEn = templateService.getSoGoodItemEn(ctx);

		Map<String, Object> params = new HashMap<>();
		params.put(NotificationConstant.TEMPLATE_KEY, NotificationConstant.APPLY_SO_GOOD_TEMPLATE_VALUE);
		params.put(NotificationConstant.CUSTOMER_NAME_EN, notifyCommon.getCustFullNameEn());
		params.put(NotificationConstant.CUSTOMER_NAME_TH, notifyCommon.getCustFullNameTH());
		params.put(NotificationConstant.NO_APPLY_SO_GOOD, soGoodWrapper.getItems().size());
		params.put(NotificationConstant.APPLY_SO_GOOD_INSTALLMENT_PLAN, soGoodWrapper.getInterestRatePercent());
		params.put(NotificationConstant.APPLY_SO_GOOD_TERM, term);
		params.put(NotificationConstant.ACCOUNT_ID, notifyCommon.getAccountId());
		params.put(NotificationConstant.SUPPORT_NO, gobalCallCenter);
		params.put(NotificationConstant.APPLY_SO_GOOD_TOTAL, soGoodTotalFormatedAmt);
		params.put(NotificationConstant.TRX_DESC_TH, totalDesTh);
		params.put(NotificationConstant.TRX_DESC_EN, totalDesEn);

		record.setParams(params);
		record.setCrmId(notifyCommon.getCrmId());
		record.setLanguage(NotificationConstant.LOCALE_TH);

		setRequestForEmailAndSms(email, phoneNo, record);

		notificationRecords.add(record);
		notificationRequest.setRecords(notificationRecords);

		TmbOneServiceResponse<NotificationResponse> sendEmailResponse = notificationClient
				.sendMessage(notifyCommon.getXCorrelationId(), notificationRequest);

		processResultLog(sendEmailResponse, notificationRequest);
	}

}