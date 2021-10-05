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
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.customer.UpdateEStatmentRequest;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.ProductHoldingsResp;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;

@Service
public class ActivitylogService {

	private static final TMBLogger<ActivitylogService> logger = new TMBLogger<>(ActivitylogService.class);

	private CreditCardLogService creditCardLogService;
	private KafkaProducerService kafkaProducerService;
	private CreditCardClient creditCardClient;
	private AccountRequestClient accountReqClient;
	private String topicName;

	public ActivitylogService(@Value("activity") String topicName, CreditCardLogService creditCardLogService,
			KafkaProducerService kafkaProducerService, CreditCardClient creditCardClient,
			AccountRequestClient accountReqClient) {
		this.creditCardLogService = creditCardLogService;
		this.kafkaProducerService = kafkaProducerService;
		this.topicName = topicName;
		this.creditCardClient = creditCardClient;
		this.accountReqClient = accountReqClient;
	}

	public void updatedEStatmentCard(Map<String, String> requestHeaders, UpdateEStatmentRequest updateEstatementReq,
			boolean result, String errorCode) {
//		
//		creditCardClient
//		
		String productName = "";
		String cardNo = "";

		CreditCardEvent updateEStatmentEvent = creditCardLogService.generateEStatementEvent(requestHeaders, productName,
				cardNo, result, errorCode);
		logActivity(updateEStatmentEvent);

	}

	public void updatedEStatmentLoan(Map<String, String> requestHeaders, UpdateEStatmentRequest updateEstatementReq,
			boolean result, String errorCode) {
		String correlationId = requestHeaders.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String crmId = requestHeaders.get(ProductsExpServiceConstant.X_CRMID);
		ResponseEntity<TmbOneServiceResponse<ProductHoldingsResp>> accountResponse = accountReqClient
				.getProductHoldingService(requestHeaders, crmId);
		
		
//		List<Object> loanAccounts = accountResponse.getBody().getData().getLoanAccounts();
//		if(CollectionUtils.isNotEmpty(loanAccounts)) {
//			for() {
//				
//			}
//		}
//		
		
		CreditCardEvent loanEvent = new CreditCardEvent(correlationId, Long.toString(System.currentTimeMillis()),
				ProductsExpServiceConstant.ESTAMENT_CONFIRM_LOAN);

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
