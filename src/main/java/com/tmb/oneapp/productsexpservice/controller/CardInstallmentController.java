
package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.creditcard.CardInstallmentResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CardInstallmentQuery;
import com.tmb.oneapp.productsexpservice.service.CacheService;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import com.tmb.oneapp.productsexpservice.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CRMID;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * @author Admin
 */
@RestController
@Api(tags = "Apply So GooOD Feature Api")
public class CardInstallmentController {

	private static final TMBLogger<CardInstallmentController> logger = new TMBLogger<>(CardInstallmentController.class);
	private final CreditCardClient creditCardClient;
	private final CreditCardLogService creditCardLogService;
	private final NotificationService notificationService;
	private final CacheService cacheService;

	/**
	 * Constructor
	 *
	 * @param
	 * @param creditCardClient
	 * @param creditCardLogService
	 */

	@Autowired
	public CardInstallmentController(CreditCardClient creditCardClient, CreditCardLogService creditCardLogService,
			NotificationService notificationService, CacheService cacheService) {
		this.creditCardClient = creditCardClient;
		this.creditCardLogService = creditCardLogService;
		this.notificationService = notificationService;
		this.cacheService = cacheService;
	}

	/**
	 * Confirm campaign transaction
	 * 
	 * @param correlationId
	 * @param requestBodyParameter
	 * @param requestHeadersParameter
	 * @return
	 * @throws TMBCommonException
	 */
	@LogAround
	@ApiOperation(value = "Confirm card installment")
	@PostMapping(value = "/creditcard/card-installment-confirm")
	@ApiImplicitParams({
			@ApiImplicitParam(name = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
			@ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000000611330", required = true, dataType = "string", paramType = "header") })
	public ResponseEntity<TmbOneServiceResponse<List<CardInstallmentResponse>>> confirmCardInstallment(
			@RequestHeader(HEADER_X_CORRELATION_ID) String correlationId,
			@RequestBody CardInstallmentQuery requestBodyParameter,
			@ApiParam(hidden = true) @RequestHeader Map<String, String> requestHeadersParameter)
			throws TMBCommonException {
		logger.info("Card installment confirm request body parameter: {}", requestBodyParameter);
		String activityId = ProductsExpServiceConstant.APPLY_SO_GOOD_ON_CLICK_CONFIRM_BUTTON;
		String crmId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CRMID);
		String activityDate = Long.toString(System.currentTimeMillis());

		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, activityDate, activityId);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

		TmbOneServiceResponse<List<CardInstallmentResponse>> oneServiceResponse = new TmbOneServiceResponse<>();
		try {
			String accountId = requestBodyParameter.getAccountId();

			if (accountId != null) {
				ResponseEntity<TmbOneServiceResponse<List<CardInstallmentResponse>>> cardInstallment = creditCardClient
						.confirmCardInstallment(correlationId, requestBodyParameter);
				TmbOneServiceResponse<List<CardInstallmentResponse>> cardInstallmentResp = cardInstallment.getBody();
				if (cardInstallmentResp != null) {
					List<CardInstallmentResponse> data = cardInstallmentResp.getData();
					if (data != null) {
						notificationService.doNotifyApplySoGood(correlationId, accountId, crmId, data,
								requestBodyParameter);
						if (successCaseMatch(correlationId, requestHeadersParameter, oneServiceResponse,
								cardInstallmentResp, data))
							return populateErrorResponse(responseHeaders, oneServiceResponse, cardInstallmentResp);

					} else {
						return populateErrorResponse(responseHeaders, oneServiceResponse, cardInstallmentResp);
					}

				}
			} else {
				return dataNotFoundErrorResponse(responseHeaders, oneServiceResponse);
			}
		} catch (Exception e) {
			creditCardEvent.setActivityStatus(ProductsExpServiceConstant.FAILURE_ACT_LOG);
			creditCardEvent.setResult(ProductsExpServiceConstant.FAILURE_ACT_LOG);
			creditCardEvent.setFailReason(e.getMessage());
			creditCardLogService.logActivity(creditCardEvent);

			logger.error("Error while confirmCardInstallment: {}", e);
			throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), HttpStatus.OK, null);
		}
		cacheService.removeCacheAfterSuccessCreditCard(correlationId, crmId);
		return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);

	}

	boolean successCaseMatch(String correlationId, Map<String, String> requestHeadersParameter,
			TmbOneServiceResponse<List<CardInstallmentResponse>> oneServiceResponse,
			TmbOneServiceResponse<List<CardInstallmentResponse>> cardInstallmentResp,
			List<CardInstallmentResponse> data) {
		return ifSuccessCaseMatch(correlationId, requestHeadersParameter, oneServiceResponse, cardInstallmentResp,
				data);

	}

	/**
	 * @param correlationId
	 * @param requestBodyParameter
	 * @param requestHeadersParameter
	 * @param oneServiceResponse
	 * @param cardInstallmentResp
	 * @param data
	 * @return
	 */
	boolean ifSuccessCaseMatch(String correlationId, Map<String, String> requestHeadersParameter,
			TmbOneServiceResponse<List<CardInstallmentResponse>> oneServiceResponse,
			TmbOneServiceResponse<List<CardInstallmentResponse>> cardInstallmentResp,
			List<CardInstallmentResponse> data) {
		creditCardLogService.generateApplySoGoodConfirmEvent(correlationId, requestHeadersParameter, data);

		boolean success = data.stream().anyMatch(t -> t.getStatus().getStatusCode().equals("0"));

		if (success) {
			successResponse(oneServiceResponse, cardInstallmentResp);
		} else {

			return true;
		}
		return false;
	}

	/**
	 * @param oneServiceResponse
	 * @param cardInstallmentResp
	 */
	void successResponse(TmbOneServiceResponse<List<CardInstallmentResponse>> oneServiceResponse,
			TmbOneServiceResponse<List<CardInstallmentResponse>> cardInstallmentResp) {
		oneServiceResponse.setData(cardInstallmentResp.getData());
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
	}

	/**
	 * @param responseHeaders
	 * @param oneServiceResponse
	 * @param cardInstallmentResp
	 * @return
	 */
	ResponseEntity<TmbOneServiceResponse<List<CardInstallmentResponse>>> populateErrorResponse(
			HttpHeaders responseHeaders, TmbOneServiceResponse<List<CardInstallmentResponse>> oneServiceResponse,
			TmbOneServiceResponse<List<CardInstallmentResponse>> cardInstallmentResp) {
		oneServiceResponse.setData(cardInstallmentResp.getData());
		oneServiceResponse
				.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(), ResponseCode.GENERAL_ERROR.getMessage(),
						ResponseCode.GENERAL_ERROR.getService(), ResponseCode.GENERAL_ERROR.getDesc()));
		return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
	}

	/**
	 * @param responseHeaders
	 * @param oneServiceResponse
	 * @return
	 */
	ResponseEntity<TmbOneServiceResponse<List<CardInstallmentResponse>>> dataNotFoundErrorResponse(
			HttpHeaders responseHeaders, TmbOneServiceResponse<List<CardInstallmentResponse>> oneServiceResponse) {
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
				ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
				ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
		return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
	}

}
