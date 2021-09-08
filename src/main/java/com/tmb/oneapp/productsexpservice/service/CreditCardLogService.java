package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.creditcard.CardInstallment;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitReq;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CardInstallmentResponse;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.ErrorStatus;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.InstallmentPlan;
import com.tmb.oneapp.productsexpservice.model.loan.HomeLoanFullInfoResponse;
import com.tmb.oneapp.productsexpservice.util.ConversionUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CreditCardLogService {
	private static final TMBLogger<CreditCardLogService> logger = new TMBLogger<>(CreditCardLogService.class);
	@Value("${com.tmb.oneapp.service.activity.topic.name}")
	private String topicName = "activity";
	private KafkaProducerService kafkaProducerService;
	private CreditCardClient creditCardClient;

	/**
	 * constructor
	 *
	 * @param topicName
	 * @param kafkaProducerService
	 */
	public CreditCardLogService(KafkaProducerService kafkaProducerService, CreditCardClient creditCardClient) {
		this.kafkaProducerService = kafkaProducerService;
		this.creditCardClient = creditCardClient;
	}

	/**
	 * Call Activity logs for activate card event
	 **/

	public CreditCardEvent callVerifyCardNoEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader) {

		logger.info("Inside callActivityBaseEvent");

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setCardNumber(reqHeader.get(ProductsExpServiceConstant.ACCOUNT_ID).substring(21, 25));
		creditCardEvent.setMethod(ProductsExpServiceConstant.METHOD);
		return creditCardEvent;
	}

	/**
	 * @param creditCardEvent
	 * @param reqHeader
	 * @return
	 */
	public CreditCardEvent callActivityBaseEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader) {

		logger.info("Inside callActivityBaseEvent");

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setCardNumber(reqHeader.get(ProductsExpServiceConstant.ACCOUNT_ID).substring(21, 25));
		creditCardEvent.setResult(ProductsExpServiceConstant.SUCCESS);
		return creditCardEvent;
	}

	/**
	 * Call activity logs for verifyCvv Event
	 *
	 * @param creditCardEvent
	 * @param requestHeadersParameter
	 * @return
	 */
	public CreditCardEvent onClickNextButtonEvent(CreditCardEvent creditCardEvent,
			Map<String, String> requestHeadersParameter, SetCreditLimitReq requestBody) {

		populateBaseEvents(creditCardEvent, requestHeadersParameter);
		creditCardEvent
				.setCardNumber(requestHeadersParameter.get(ProductsExpServiceConstant.ACCOUNT_ID).substring(21, 25));
		creditCardEvent.setCurrentLimit(requestBody.getCurrentCreditLimit());
		creditCardEvent.setNewLimit((requestBody.getPreviousCreditLimit()));
		creditCardEvent.setType(ProductsExpServiceConstant.CHANGE_TYPE_PERMANENT);
		return creditCardEvent;
	}

	/**
	 * @param creditCardEvent
	 * @param reqHeader
	 * @param requestBody
	 * @param mode
	 * @return
	 */
	public CreditCardEvent onClickNextButtonLimitEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader,
			SetCreditLimitReq requestBody, String mode) {

		if (mode.equalsIgnoreCase(ProductsExpServiceConstant.MODE_PERMANENT)) {
			creditCardEvent.setCardNumber(requestBody.getAccountId().substring(21, 25));
			creditCardEvent.setNewLimit(formateForCurrency(requestBody.getCurrentCreditLimit()));
			creditCardEvent.setCurrentLimit(formateForCurrency(requestBody.getPreviousCreditLimit()));
			creditCardEvent.setType("Adjust Permanent Limit");

		} else if (mode.equalsIgnoreCase(ProductsExpServiceConstant.MODE_TEMPORARY)) {
			creditCardEvent.setCardNumber(requestBody.getAccountId().substring(21, 25));
			creditCardEvent.setExpiryDateForTempRequest(requestBody.getExpiryDate());
			creditCardEvent.setReasonForRequest(requestBody.getReasonDescEn());
			creditCardEvent.setType("Request Temporary Limit");
		}
		populateBaseEvents(creditCardEvent, reqHeader);

		return creditCardEvent;
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
	 * @param creditCardEvent
	 * @param reqHeader
	 * @param requestBody
	 * @return
	 */
	public CreditCardEvent completeUsageListEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader,
			SetCreditLimitReq requestBody,String result) {

		populateBaseEvents(creditCardEvent, reqHeader);
		creditCardEvent.setCardNumber(requestBody.getAccountId().substring(21, 25));

		creditCardEvent.setResult(result);
		return creditCardEvent;
	}

	/**
	 * @param creditCardEvent
	 * @param reqHeader
	 * @return
	 */
	public CreditCardEvent onVerifyPinEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader) {

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setCardNumber(reqHeader.get(ProductsExpServiceConstant.ACCOUNT_ID).substring(21, 25));
		creditCardEvent.setResult(ProductsExpServiceConstant.SUCCESS);
		return creditCardEvent;
	}

	/**
	 * Call activity logs for verifyCvv Event
	 *
	 * @param creditCardEvent
	 * @param reqHeader
	 * @return
	 */
	public CreditCardEvent verifyCvvBaseEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader) {

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setCardNumber(reqHeader.get(ProductsExpServiceConstant.ACCOUNT_ID).substring(21, 25));
		return creditCardEvent;
	}

	/**
	 * @param correlationId
	 * @param reqHeader
	 * @param requestBody
	 * @param data
	 */
	public void generateApplySoGoodConfirmEvent(String correlationId, Map<String, String> reqHeader,
			List<CardInstallmentResponse> data) {

		if (CollectionUtils.isNotEmpty(data)) {
			List<CardInstallmentResponse> sucessResponse = data.stream().filter( e->"0".equals(e.getStatus().getStatusCode())).collect(Collectors.toList());
			List<CardInstallmentResponse> failResponse = data.stream().filter( e->"1".equals(e.getStatus().getStatusCode())).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(sucessResponse) && CollectionUtils.isNotEmpty(failResponse)) {
				constructCardEvent(correlationId,reqHeader,sucessResponse.get(0));
			}else {
				if(CollectionUtils.isNotEmpty(sucessResponse)) {
					constructCardEvent(correlationId,reqHeader,sucessResponse.get(0));
				}else {
					constructCardEvent(correlationId,reqHeader,failResponse.get(0));
				}
			}
		}

	}

	/**
	 * Construct installment confirm event in activity log
	 * 
	 * @param correlationId
	 * @param reqHeader
	 * @param e
	 */
	private void constructCardEvent(String correlationId, Map<String, String> reqHeader, CardInstallmentResponse e) {
		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, Long.toString(System.currentTimeMillis()),
				ProductsExpServiceConstant.APPLY_SO_GOOD_ON_CLICK_CONFIRM_BUTTON);

		creditCardEvent.setCardNumber("xx" + e.getCreditCard().getAccountId().substring(21, 25));
		populateBaseEvents(creditCardEvent, reqHeader);
		if (Objects.nonNull(e.getStatus()) && "0".equals(e.getStatus().getStatusCode())) {
			CardInstallment cardInstallment = e.getCreditCard().getCardInstallment();
			creditCardEvent.setPlan(converPlan(cardInstallment, correlationId));
			creditCardEvent.setTransactionDescription(cardInstallment.getTransactionDescription());

			Double amountInDouble = ConversionUtil.stringToDouble(cardInstallment.getAmounts());
			Double installmentInDouble = ConversionUtil.stringToDouble(cardInstallment.getMonthlyInstallments());

			Double interestInDouble = ConversionUtil.stringToDouble(cardInstallment.getInterest());
			Double amountPlusTotalInterest = amountInDouble + interestInDouble;

			creditCardEvent.setResult(ProductsExpServiceConstant.SUCCESS);
			creditCardEvent.setActivityStatus(ProductsExpServiceConstant.SUCCESS);

			creditCardEvent.setAmountMonthlyInstallment(formateForCurrency(amountInDouble.toString()) + "+"
					+ formateForCurrency(installmentInDouble.toString()));

			creditCardEvent.setTotalAmountTotalIntrest(formateForCurrency(interestInDouble.toString()) + "+"
					+ formateForCurrency(amountPlusTotalInterest.toString()));
		} else {
			creditCardEvent.setResult(ProductsExpServiceConstant.FAILURE);
			creditCardEvent.setActivityStatus(ProductsExpServiceConstant.FAILURE);
			ErrorStatus errorStatus = e.getStatus().getErrorStatus().get(0);
			creditCardEvent.setReasonForRequest(errorStatus.getErrorCode());
		}
		logActivity(creditCardEvent);

	}

	/**
	 * Generate format plan
	 * 
	 * @param cardInstallment
	 * @param correlationId
	 * @return
	 */
	private String converPlan(CardInstallment cardInstallment, String correlationId) {
		StringBuffer bf = new StringBuffer();
		ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> responseInstallments = creditCardClient
				.getInstallmentPlan(correlationId);
		List<InstallmentPlan> installmentPlans = responseInstallments.getBody().getData();
		InstallmentPlan reqInstallmentPlan = null;
		for (InstallmentPlan installmentplan : installmentPlans) {
			if (installmentplan.getInstallmentsPlan().equals(cardInstallment.getPromotionModelNo())) {
				reqInstallmentPlan = installmentplan;
			}
		}
		if (Objects.nonNull(reqInstallmentPlan)) {
			bf.append(formateForCurrency(reqInstallmentPlan.getInterestRate()) + "%");
			bf.append(StringUtils.SPACE);
			bf.append(reqInstallmentPlan.getPaymentTerm());
			bf.append(StringUtils.SPACE);
			bf.append("Months");
		}

		return bf.toString();
	}

	/**
	 * method for populating base events for Activity logs
	 *
	 * @param creditCardEvent
	 * @param reqHeader
	 */
	private void populateBaseEvents(CreditCardEvent creditCardEvent, Map<String, String> reqHeader) {
		creditCardEvent.setIpAddress(reqHeader.get(ProductsExpServiceConstant.X_FORWARD_FOR));
		creditCardEvent.setOsVersion(reqHeader.get(ProductsExpServiceConstant.OS_VERSION));
		creditCardEvent.setChannel(reqHeader.get(ProductsExpServiceConstant.CHANNEL));
		creditCardEvent.setAppVersion(reqHeader.get(ProductsExpServiceConstant.APP_VERSION));
		creditCardEvent.setCrmId(reqHeader.get(ProductsExpServiceConstant.X_CRMID));
		creditCardEvent.setDeviceId(reqHeader.get(ProductsExpServiceConstant.DEVICE_ID));
		creditCardEvent.setDeviceModel(reqHeader.get(ProductsExpServiceConstant.DEVICE_MODEL));
		creditCardEvent
				.setCorrelationId(reqHeader.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID.toLowerCase()));
		creditCardEvent.setActivityStatus(ProductsExpServiceConstant.SUCCESS);
	}

	/**
	 * Method sending data to Kafka producer
	 *
	 * @param loginEvent
	 */
	@Async
	@LogAround
	public void logActivity(CreditCardEvent loginEvent) {
		try {
			String output = TMBUtils.convertJavaObjectToString(loginEvent);
			logger.info("############  Current Date : {}", loginEvent.getActivityDate());
			logger.info("Activity Log Payload : {}", output);
			kafkaProducerService.sendMessageAsync(topicName, output);
			logger.info("callPostEventService -  data posted to event_service : {}", System.currentTimeMillis());
		} catch (Exception e) {
			logger.info("Unable to process the request : {}", e);
		}
	}

	/**
	 * Activity log for finish block card
	 *
	 * @param status
	 * @param activityId
	 * @param correlationId
	 * @param activityDate
	 * @param accountId
	 */
	@Async
	@LogAround
	public void finishBlockCardActivityLog(String status, String activityId, String correlationId, String activityDate,
			String accountId, String failReason) {
		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, activityDate, activityId);
		if (status.equalsIgnoreCase(ProductsExpServiceConstant.FAILURE)) {
			creditCardEvent.setFailReason(failReason);
		}

		creditCardEvent.setCardNumber(accountId.substring(21, 25));
		creditCardEvent.setActivityStatus(status);
		logActivity(creditCardEvent);
	}

	/**
	 * @param status
	 * @param activityId
	 * @param correlationId
	 * @param activityDate
	 * @param accountId
	 * @param failReason
	 */
	@Async
	@LogAround
	public void finishSetPinActivityLog(String status, String activityId, String correlationId, String activityDate,
			String accountId, String failReason) {
		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, activityDate, activityId);
		if (status.equalsIgnoreCase(ProductsExpServiceConstant.FAILURE)) {
			creditCardEvent.setFailReason(ProductsExpServiceConstant.FAILED);
		}
		creditCardEvent.setResult(status);
		creditCardEvent.setCardNumber(accountId.substring(21, 25));
		creditCardEvent.setActivityStatus(status);
		logActivity(creditCardEvent);
	}

	/**
	 * @param creditCardEvent
	 * @param reqHeader
	 * @param response
	 * @return
	 */
	public CreditCardEvent viewLoanLandingScreenEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader,
			HomeLoanFullInfoResponse response) {

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setLoanNumber(response.getAccount().getId().substring(1, 11));
		creditCardEvent.setProductName(response.getProductConfig().getProductNameEN());
		return creditCardEvent;
	}

	/**
	 * @param creditCardEvent
	 * @param reqHeader
	 * @param fetchCardResponse
	 * @return
	 */
	public CreditCardEvent loadCardDetailsEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader,
			FetchCardResponse fetchCardResponse) {

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setCardNumber(fetchCardResponse.getCreditCard().getAccountId().substring(21, 25));
		creditCardEvent.setProductName(fetchCardResponse.getProductCodeData().getProductNameEN());
		return creditCardEvent;
	}

}