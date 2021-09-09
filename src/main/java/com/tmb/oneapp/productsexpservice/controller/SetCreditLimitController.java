package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitReq;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitResp;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import com.tmb.oneapp.productsexpservice.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;

/**
 * SetCreditLimitController request mapping will handle apis call and then
 * navigate to respective method
 */
@RestController
@Api(tags = "Temporary and Permanent Credit Card Limit")
public class SetCreditLimitController {
	private final CreditCardClient creditCardClient;
	private static final TMBLogger<SetCreditLimitController> logger = new TMBLogger<>(SetCreditLimitController.class);
	private CreditCardLogService creditCardLogService;
	private NotificationService notificationService;

	/**
	 * Constructor
	 *
	 * @param creditCardClient
	 */
	@Autowired
	public SetCreditLimitController(CreditCardClient creditCardClient, CreditCardLogService creditCardLogService,
			NotificationService notificationService) {
		this.creditCardClient = creditCardClient;
		this.creditCardLogService = creditCardLogService;
		this.notificationService = notificationService;
	}

	/**
	 * Temporary and Permanent Credit Card Limit api
	 *
	 * @param requestBodyParameter
	 * @return status code
	 */
	@LogAround
	@ApiOperation(value = "Temporary and Permanent Credit Card Limit")
	@PostMapping(value = "/credit-card/set-credit-limit")
	@ApiImplicitParams({
			@ApiImplicitParam(name = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da") })

	public ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> setCreditLimit(
			@RequestBody SetCreditLimitReq requestBodyParameter,
			@RequestHeader Map<String, String> requestHeadersParameter) {
		logger.info("Request Parameter fetchCreditLimit : {} ", requestBodyParameter);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<SetCreditLimitResp> oneServiceResponse = new TmbOneServiceResponse<>();
		String mode = requestBodyParameter.getMode();

		String correlationId = requestHeadersParameter.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String activityDate = Long.toString(System.currentTimeMillis());
		String accountId = requestBodyParameter.getAccountId();
		String crmId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CRMID);

		try {

			CreditCardEvent creditCardRequestAdjustEvent = new CreditCardEvent(correlationId, activityDate,
					ProductsExpServiceConstant.CHANGE_TEMP_COMPLETE_ADJUST_USAGE_LIMIT);

			ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> response = creditCardClient
					.setCreditLimit(correlationId, requestBodyParameter);

			if (response.getBody().getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
				creditCardRequestAdjustEvent = creditCardLogService.completeUsageListEvent(creditCardRequestAdjustEvent,
						requestHeadersParameter, requestBodyParameter, ProductsExpServiceConstant.SUCCESS);
				oneServiceResponse
						.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
								ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
				oneServiceResponse.setData(response.getBody().getData());

				if (mode.equalsIgnoreCase(ProductsExpServiceConstant.MODE_PERMANENT)) {
					activityDate = Long.toString(System.currentTimeMillis());
					CreditCardEvent successPermanent = new CreditCardEvent(correlationId.toLowerCase(Locale.ROOT),
							activityDate, ProductsExpServiceConstant.ACTIVITY_ID_CHANGE_USAGE_PERMANENT);
					successPermanent.setCardNumber(accountId.substring(21, 25));
					successPermanent.setResult(ProductsExpServiceConstant.SUCCESS);
					successPermanent = creditCardLogService.onClickNextButtonLimitEvent(creditCardRequestAdjustEvent,
							requestHeadersParameter, requestBodyParameter, ProductsExpServiceConstant.MODE_PERMANENT);

					/* Activity log -- MODE_PERMANENT */
					creditCardLogService.logActivity(successPermanent);
					notificationService.doNotifySuccessForChangeUsageLimit(correlationId, accountId, crmId,
							requestBodyParameter);
				} else if (mode.equalsIgnoreCase(ProductsExpServiceConstant.MODE_TEMPORARY)) {
					activityDate = Long.toString(System.currentTimeMillis());
					CreditCardEvent successTemporary = new CreditCardEvent(correlationId.toLowerCase(Locale.ROOT),
							activityDate, ProductsExpServiceConstant.ACTIVITY_ID_CHANGE_USAGE_TEMPORARY);
					successTemporary.setCardNumber(accountId.substring(21, 25));
					successTemporary.setResult(ProductsExpServiceConstant.SUCCESS);
					successTemporary = creditCardLogService.onClickNextButtonLimitEvent(creditCardRequestAdjustEvent,
							requestHeadersParameter, requestBodyParameter, ProductsExpServiceConstant.MODE_TEMPORARY);

					/* Activity log -- MODE_TEMPORARY */
					creditCardLogService.logActivity(successTemporary);
					notificationService.doNotifySuccessForTemporaryLimit(correlationId, accountId, crmId,
							requestBodyParameter);
				}

			} else {
				creditCardRequestAdjustEvent = creditCardLogService.completeUsageListEvent(creditCardRequestAdjustEvent,
						requestHeadersParameter, requestBodyParameter, ProductsExpServiceConstant.FAILED);
				creditCardRequestAdjustEvent.setReasonForRequest(
						response.getBody().getData().getStatus().getErrorStatus().get(0).getErrorCode());
				oneServiceResponse
						.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
								ResponseCode.FAILED.getService(), ResponseCode.FAILED.getDesc()));
				oneServiceResponse.setData(response.getBody().getData());

				if (mode.equalsIgnoreCase(ProductsExpServiceConstant.MODE_PERMANENT)) {
					activityDate = Long.toString(System.currentTimeMillis());
					CreditCardEvent failPermanent = new CreditCardEvent(correlationId.toLowerCase(Locale.ROOT),
							activityDate, ProductsExpServiceConstant.ACTIVITY_ID_CHANGE_USAGE_PERMANENT);

					failPermanent.setResult(ProductsExpServiceConstant.FAILED);
					failPermanent = creditCardLogService.onClickNextButtonLimitEvent(failPermanent,
							requestHeadersParameter, requestBodyParameter, ProductsExpServiceConstant.MODE_PERMANENT);

					/* Activity log -- MODE_PERMANENT */
					creditCardLogService.logActivity(failPermanent);
					notificationService.doNotifySuccessForChangeUsageLimit(correlationId, accountId, crmId,
							requestBodyParameter);
				} else if (mode.equalsIgnoreCase(ProductsExpServiceConstant.MODE_TEMPORARY)) {
					activityDate = Long.toString(System.currentTimeMillis());
					CreditCardEvent failTemporary = new CreditCardEvent(correlationId.toLowerCase(Locale.ROOT),
							activityDate, ProductsExpServiceConstant.ACTIVITY_ID_CHANGE_USAGE_TEMPORARY);

					failTemporary.setResult(ProductsExpServiceConstant.FAILED);
					failTemporary = creditCardLogService.onClickNextButtonLimitEvent(failTemporary,
							requestHeadersParameter, requestBodyParameter, ProductsExpServiceConstant.MODE_TEMPORARY);

					/* Activity log -- MODE_TEMPORARY */
					creditCardLogService.logActivity(failTemporary);
					notificationService.doNotifySuccessForTemporaryLimit(correlationId, accountId, crmId,
							requestBodyParameter);
				}

			}
			/* Activity log -- CHANGE_TEMP_COMPLETE_ADJUST_USAGE_LIMIT */
			creditCardLogService.logActivity(creditCardRequestAdjustEvent);
			return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
		} catch (Exception ex) {
			logger.error("Unable to fetch set credit limit response: {}", ex);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
					ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));
			return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
		}

	}

}