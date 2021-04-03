package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitReq;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CardInstallment;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CardInstallmentQuery;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CardInstallmentResponse;
import com.tmb.oneapp.productsexpservice.model.loan.LoanDetailsFullResponse;
import com.tmb.oneapp.productsexpservice.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Class responsible for Putting activity logs in Creditcard service
 *
 */

/**
 * @author Admin
 *
 */
/**
 * @author Admin
 *
 */
@Service
public class CreditCardLogService {
	private static TMBLogger<CreditCardLogService> logger = new TMBLogger<>(CreditCardLogService.class);
	private final String topicName;
	private final KafkaProducerService kafkaProducerService;

	/**
	 * constructor
	 *
	 * @param topicName
	 * @param kafkaProducerService
	 */
	public CreditCardLogService(@Value("activity") String topicName, KafkaProducerService kafkaProducerService) {
		this.topicName = topicName;
		this.kafkaProducerService = kafkaProducerService;
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
			creditCardEvent.setNewLimit(requestBody.getCurrentCreditLimit());
			creditCardEvent.setCurrentLimit(requestBody.getPreviousCreditLimit());
			creditCardEvent.setType(requestBody.getType());

		} else if (mode.equalsIgnoreCase(ProductsExpServiceConstant.MODE_TEMPORARY)) {
			creditCardEvent.setCardNumber(requestBody.getAccountId().substring(21, 25));
			creditCardEvent.setExpiryDateForTempRequest(requestBody.getExpiryDate());
			creditCardEvent.setReasonForRequest(requestBody.getReasonDescEn());
			creditCardEvent.setType(requestBody.getType());
		}
		populateBaseEvents(creditCardEvent, reqHeader);

		return creditCardEvent;
	}

	/**
	 * @param creditCardEvent
	 * @param reqHeader
	 * @param requestBody
	 * @return
	 */
	public CreditCardEvent completeUsageListEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader,
												  SetCreditLimitReq requestBody) {

		populateBaseEvents(creditCardEvent, reqHeader);
		creditCardEvent.setCardNumber(requestBody.getAccountId().substring(21, 25));

		creditCardEvent.setResult(ProductsExpServiceConstant.SUCCESS);
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
	 * @return
	 */
	public void applySoGoodConfirmEvent(String correlationId, Map<String, String> reqHeader, CardInstallmentQuery requestBody, List<CardInstallmentResponse> data) {


		List<CardInstallment> cardInstallment = requestBody.getCardInstallment();

		for (CardInstallment installment : cardInstallment) {
			CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, Long.toString(System.currentTimeMillis()), ProductsExpServiceConstant.APPLY_SO_GOOD_ON_CLICK_CONFIRM_BUTTON);
			creditCardEvent.setPlan(installment.getPromotionModelNo());
			creditCardEvent.setTransactionDescription(installment.getTransactionDescription());
			populateBaseEvents(creditCardEvent, reqHeader);

			Double amountInDouble = ConversionUtil.stringToDouble(installment.getAmounts());
			Double installmentInDouble = ConversionUtil.stringToDouble(installment.getMonthlyInstallments());
			Double installmentPlusAmount = amountInDouble + installmentInDouble;

			Double interestInDouble = ConversionUtil.stringToDouble(installment.getInterest());
			Double amountPlusTotalInterest = amountInDouble + interestInDouble;
			creditCardEvent.setCardNumber(requestBody.getAccountId().substring(21, 25));
			creditCardEvent.setResult(ProductsExpServiceConstant.SUCCESS);
			String amountPlusMonthlyInstallment = ConversionUtil.doubleToString(installmentPlusAmount);
			creditCardEvent.setAmountPlusMonthlyInstallment(amountPlusMonthlyInstallment);

			String totalAmountPlusTotalInterest = ConversionUtil.doubleToString(amountPlusTotalInterest);

			creditCardEvent.setTotalAmountPlusTotalIntrest(totalAmountPlusTotalInterest);

			for(CardInstallmentResponse cardResp : data) {
				String transactionKey = cardResp.getCreditCard().getCardInstallment().getTransactionKey();

				if(cardResp.getStatus().getErrorStatus()!=null && !cardResp.getStatus().getErrorStatus().isEmpty()  &&  
						transactionKey.equalsIgnoreCase(installment.getTransactionKey())) {

					creditCardEvent.setResult(ProductsExpServiceConstant.FAILURE);
					creditCardEvent.setActivityStatus(ProductsExpServiceConstant.FAILURE);
					creditCardEvent.setFailReason(cardResp.getStatus().getErrorStatus().get(0).getDescription());
					creditCardEvent.setReasonForRequest(cardResp.getStatus().getErrorStatus().get(0).getDescription());
				}

			}

			logActivity(creditCardEvent);

		}

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
			creditCardEvent.setCorrelationId(reqHeader.get(ProductsExpServiceConstant.X_CORRELATION_ID.toLowerCase()));
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

	public CreditCardEvent viewLoanLandingScreenEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader, LoanDetailsFullResponse response) {

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setLoanNumber(response.getAccount().getId().substring(1, 11));
		creditCardEvent.setProductName(response.getProductConfig().getProductNameEN());
		return creditCardEvent;
	}

	public CreditCardEvent loadCardDetailsEvent(CreditCardEvent creditCardEvent, Map<String, String> reqHeader, FetchCardResponse fetchCardResponse) {

		populateBaseEvents(creditCardEvent, reqHeader);

		creditCardEvent.setCardNumber(fetchCardResponse.getCreditCard().getAccountId().substring(21,25));
		creditCardEvent.setProductName(fetchCardResponse.getProductCodeData().getProductNameEN());
		return creditCardEvent;
	}


}