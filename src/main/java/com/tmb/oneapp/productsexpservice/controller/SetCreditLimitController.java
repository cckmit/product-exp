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

	/**
	 * Constructor
	 *
	 * @param creditCardClient
	 */
	@Autowired
	public SetCreditLimitController(CreditCardClient creditCardClient, CreditCardLogService creditCardLogService) {
		this.creditCardClient = creditCardClient;
		this.creditCardLogService = creditCardLogService;
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
			@ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da") })

	public ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> setCreditLimit(
			@RequestBody SetCreditLimitReq requestBodyParameter,
			@RequestHeader Map<String, String> requestHeadersParameter) {
		logger.info("Request Parameter fetchCreditLimit : {} ", requestBodyParameter);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<SetCreditLimitResp> oneServiceResponse = new TmbOneServiceResponse<>();
		String mode = requestBodyParameter.getMode();

		String correlationId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CORRELATION_ID);
		String activityDate = Long.toString(System.currentTimeMillis());
		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, activityDate,
				ProductsExpServiceConstant.CHANGE_TEMP_COMPLETE_ADJUST_USAGE_LIMIT);
		creditCardEvent = creditCardLogService.completeUsageListEvent(creditCardEvent, requestHeadersParameter,
				requestBodyParameter);
		try {
			ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> response = creditCardClient
					.fetchSetCreditLimit(correlationId, requestBodyParameter);

			/* Activity log -- CHANGE_TEMP_COMPLETE_ADJUST_USAGE_LIMIT */
			creditCardLogService.logActivity(creditCardEvent);

			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
			oneServiceResponse.setData(response.getBody().getData());
			if (mode.equalsIgnoreCase(ProductsExpServiceConstant.MODE_PERMANENT)) {
				activityDate = Long.toString(System.currentTimeMillis());
				creditCardEvent = new CreditCardEvent(correlationId.toLowerCase(Locale.ROOT), activityDate,
						ProductsExpServiceConstant.ACTIVITY_ID_TEMP);

				creditCardEvent = creditCardLogService.onClickNextButtonLimitEvent(creditCardEvent,
						requestHeadersParameter, requestBodyParameter, ProductsExpServiceConstant.MODE_PERMANENT);

				/* Activity log -- MODE_PERMANENT */
				creditCardLogService.logActivity(creditCardEvent);
			} else if (mode.equalsIgnoreCase(ProductsExpServiceConstant.MODE_TEMPORARY)) {
				activityDate = Long.toString(System.currentTimeMillis());
				creditCardEvent = new CreditCardEvent(correlationId.toLowerCase(Locale.ROOT), activityDate,
						ProductsExpServiceConstant.ACTIVITY_ID_TEMP_REASON_OF_REQUEST);

				creditCardEvent = creditCardLogService.onClickNextButtonLimitEvent(creditCardEvent,
						requestHeadersParameter, requestBodyParameter, ProductsExpServiceConstant.MODE_TEMPORARY);

				/* Activity log -- MODE_TEMPORARY */
				creditCardLogService.logActivity(creditCardEvent);
			}
			return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
		} catch (Exception ex) {
			logger.error("Unable to fetch set credit limit response: {}", ex);

			// change credit limit for temp
			CreditCardEvent creditCardTempLimit = new CreditCardEvent(correlationId.toLowerCase(Locale.ROOT),
					activityDate, ProductsExpServiceConstant.ACTIVITY_ID_TEMP_REASON_OF_REQUEST);
			creditCardTempLimit = creditCardLogService.onClickNextButtonLimitEvent(creditCardTempLimit,
					requestHeadersParameter, requestBodyParameter, ProductsExpServiceConstant.MODE_TEMPORARY);
			creditCardTempLimit.setFailReason(ex.getMessage()!=null?ex.getMessage():"");
			creditCardTempLimit.setActivityStatus(ProductsExpServiceConstant.FAILURE);
			creditCardLogService.logActivity(creditCardTempLimit);

			// complete usage limit
			creditCardEvent = creditCardLogService.onClickNextButtonLimitEvent(creditCardEvent, requestHeadersParameter,
					requestBodyParameter, ProductsExpServiceConstant.MODE_TEMPORARY);
			creditCardEvent = creditCardLogService.completeUsageListEvent(creditCardEvent, requestHeadersParameter,
					requestBodyParameter);
			creditCardEvent.setFailReason(ex.getMessage()!=null?ex.getMessage():"");
			creditCardEvent.setActivityStatus(ProductsExpServiceConstant.FAILURE);
			creditCardEvent.setResult(ProductsExpServiceConstant.FAILURE);
			/* Activity log */
			creditCardLogService.logActivity(creditCardEvent);

			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
					ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));
			return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
		}

	}

}