package com.tmb.oneapp.productsexpservice.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.BaseEvent;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.customer.UpdateEStatmentRequest;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;

@Service
public class ActivitylogService {

	private static final TMBLogger<ActivitylogService> logger = new TMBLogger<>(ActivitylogService.class);

	private CreditCardLogService creditCardLogService;
	private KafkaProducerService kafkaProducerService;
	private CreditCardClient creditCardClient;
	private CommonServiceClient commonServiceClient;
	private String topicName;

	public ActivitylogService(@Value("activity") String topicName, CreditCardLogService creditCardLogService,
			KafkaProducerService kafkaProducerService, CreditCardClient creditCardClient,
			CommonServiceClient commonServiceClient) {
		this.creditCardLogService = creditCardLogService;
		this.kafkaProducerService = kafkaProducerService;
		this.topicName = topicName;
		this.creditCardClient = creditCardClient;
		this.commonServiceClient = commonServiceClient;
	}

	public void updatedEStatmentCard(Map<String, String> requestHeaders, UpdateEStatmentRequest updateEstatementReq,
			boolean result, String errorCode) {
		String correlationId = requestHeaders.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String productName = constructProductNameInfomation(correlationId, updateEstatementReq);
		ResponseEntity<FetchCardResponse> fetchCardInfoResp = creditCardClient.getCreditCardDetails(correlationId,
				updateEstatementReq.getAccountId());
		String cardNo = "xx"+fetchCardInfoResp.getBody().getCreditCard().getAccountId().substring(21, 25);
		CreditCardEvent updateEStatmentEvent = creditCardLogService.generateEStatementEvent(requestHeaders, productName,
				cardNo, result, errorCode);
		constructCommonDetail(requestHeaders, updateEStatmentEvent);
		updateEStatmentEvent.setReasonForRequest(errorCode);
		updateEStatmentEvent.setResult(result ? ProductsExpServiceConstant.SUCCESS : ProductsExpServiceConstant.FAILED);
		logActivity(updateEStatmentEvent);

	}

	/**
	 * Construct product name
	 * 
	 * @param correlationId
	 * @param updateEstatementReq
	 * @return
	 */
	private String constructProductNameInfomation(String correlationId, UpdateEStatmentRequest updateEstatementReq) {

		String productCodeName = "";
		ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> response = commonServiceClient
				.getProductConfig(correlationId);

		List<ProductConfig> productConfigs = response.getBody().getData();
		if (CollectionUtils.isNotEmpty(productConfigs)) {
			for (ProductConfig productInfo : productConfigs) {
				if (CollectionUtils.isNotEmpty(updateEstatementReq.getProductType())) {
					if (updateEstatementReq.getProductType().get(0).equals(productInfo.getProductCode())) {
						productCodeName = "(" + productInfo.getProductCode() + ")  " + productInfo.getProductNameEN();
					}
				}
			}
		}
		return productCodeName;

	}

	/**
	 * Update EStatment
	 * 
	 * @param requestHeaders
	 * @param updateEstatementReq
	 * @param result
	 * @param errorCode
	 */
	public void updatedEStatmentLoan(Map<String, String> requestHeaders, UpdateEStatmentRequest updateEstatementReq,
			boolean result, String errorCode) {
		String correlationId = requestHeaders.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String productCodeName = constructProductNameInfomation(correlationId, updateEstatementReq);
		CreditCardEvent loanEvent = new CreditCardEvent(correlationId, Long.toString(System.currentTimeMillis()),
				ProductsExpServiceConstant.ESTAMENT_CONFIRM_LOAN);
		constructCommonDetail(requestHeaders, loanEvent);
		loanEvent.setProductName(productCodeName);
		loanEvent.setLoanNumber(updateEstatementReq.getLoanId());
		loanEvent.setReasonForRequest(errorCode);
		loanEvent.setResult(result ? ProductsExpServiceConstant.SUCCESS : ProductsExpServiceConstant.FAILED);

		loanEvent.setActivityStatus(
				result ? ProductsExpServiceConstant.SUCCESS : ProductsExpServiceConstant.FAILURE_ACT_LOG);
		logActivity(loanEvent);

	}

	/**
	 * Construct common detail supported
	 * 
	 * @param requestHeaders
	 */
	private void constructCommonDetail(Map<String, String> requestHeaders, BaseEvent baseEvent) {
		baseEvent.setIpAddress(requestHeaders.get(ProductsExpServiceConstant.X_FORWARD_FOR));
		baseEvent.setOsVersion(requestHeaders.get(ProductsExpServiceConstant.OS_VERSION));
		baseEvent.setChannel(requestHeaders.get(ProductsExpServiceConstant.CHANNEL));
		baseEvent.setAppVersion(requestHeaders.get(ProductsExpServiceConstant.APP_VERSION));
		baseEvent.setCrmId(requestHeaders.get(ProductsExpServiceConstant.X_CRMID));
		baseEvent.setDeviceId(requestHeaders.get(ProductsExpServiceConstant.DEVICE_ID));
		baseEvent.setDeviceModel(requestHeaders.get(ProductsExpServiceConstant.DEVICE_MODEL));
		baseEvent
				.setCorrelationId(requestHeaders.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID.toLowerCase()));

	}

	/**
	 * log activiy to kafka direct
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

}
