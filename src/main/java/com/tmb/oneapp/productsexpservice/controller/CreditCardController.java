package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.GetCardBlockCodeResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.VerifyCreditCardResponse;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * CreditCardController request mapping will handle apis call
 * and then navigate to respective method
 *
 */
@RestController
@Api(tags = "Verify Credit Card Details Api")
public class CreditCardController {
	private final CreditCardClient creditCardClient;
	private static final TMBLogger<CreditCardController> logger = new TMBLogger<>(
			CreditCardController.class);
	private final CreditCardLogService creditCardLogService;

	/**
	 * Constructor
	 *
	 * @param creditCardClient
	 * @param creditCardLogService
	 */

	@Autowired
	public CreditCardController(CreditCardClient creditCardClient, CreditCardLogService creditCardLogService) {
		super();
		this.creditCardClient = creditCardClient;
		this.creditCardLogService = creditCardLogService;
	}

	/**
	 * verify block code and get card details
	 *
	 * @param requestHeadersParameter
	 * @return block code ,credit card id , expiry date
	 */
	@LogAround
	@ApiOperation(value = "Verify Credit Card Details Api")
	@PostMapping(value = "/credit-card/verifycreditcard")
	@ApiImplicitParams({
			@ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da"),
			@ApiImplicitParam(name = "account-id", value = "Account Id", required = true, dataType = "string", paramType = "header", example = "0000000050078360018000167") })

	public ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> verifyCreditCardDetails(
			@ApiParam(hidden = true) @RequestHeader Map<String, String> requestHeadersParameter) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<VerifyCreditCardResponse> oneServiceResponse = new TmbOneServiceResponse<>();
		String activityId = ProductsExpServiceConstant.ACTIVITY_ID_VERIFY_CARD_NO;
		String activityDate = Long.toString(System.currentTimeMillis());
		CreditCardEvent creditCardEvent = new CreditCardEvent(requestHeadersParameter.get(ProductsExpServiceConstant.X_CORRELATION_ID.toLowerCase()), activityDate, activityId);
		try {
			String accountId = requestHeadersParameter.get(ProductsExpServiceConstant.ACCOUNT_ID);
			String correlationId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CORRELATION_ID);
			if (!Strings.isNullOrEmpty(accountId) && !Strings.isNullOrEmpty(correlationId)) {
				ResponseEntity<GetCardBlockCodeResponse> blockCodeRes = creditCardClient
						.getCardBlockCode(correlationId, accountId);

				creditCardEvent = creditCardLogService.callVerifyCardNoEvent(creditCardEvent, requestHeadersParameter);

				/*  Activity log */
				creditCardLogService.logActivity(creditCardEvent);

				if (blockCodeRes != null && blockCodeRes.getStatusCode() == HttpStatus.OK
						&& blockCodeRes.getBody().getStatus().getStatusCode() == ProductsExpServiceConstant.ZERO) {
					return handlingResponseData(blockCodeRes, oneServiceResponse, correlationId, accountId,
							responseHeaders);

				} else {
					creditCardEvent = creditCardLogService.callVerifyCardNoEvent(creditCardEvent, requestHeadersParameter);
					creditCardEvent.setActivityStatus(ProductsExpServiceConstant.FAILURE);
					/*  Activity log */
					creditCardLogService.logActivity(creditCardEvent);
					return this.handlingFailedResponse(oneServiceResponse, responseHeaders);
				}

			} else {
				creditCardEvent = creditCardLogService.callVerifyCardNoEvent(creditCardEvent, requestHeadersParameter);
                creditCardEvent.setActivityStatus(ProductsExpServiceConstant.FAILURE);
				/*  Activity log */
				creditCardLogService.logActivity(creditCardEvent);
				oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
						ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
						ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
				return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
			}

		} catch (Exception ex) {
			logger.error("Unable to fetch verify block code and get card details : {}", ex);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
					ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));

			return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
		}

	}

	/**
	 * Description:- handling response
	 *
	 * @param blockCodeRes
	 * @param oneServiceResponse
	 * @param correlationId
	 * @param accountId
	 * @param responseHeaders
	 *
	 */
	public ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> handlingResponseData(
			ResponseEntity<GetCardBlockCodeResponse> blockCodeRes,
			TmbOneServiceResponse<VerifyCreditCardResponse> oneServiceResponse, String correlationId, String accountId,
			HttpHeaders responseHeaders) {

		VerifyCreditCardResponse response = new VerifyCreditCardResponse();
		GetCardBlockCodeResponse oneBlockCodeRes = blockCodeRes.getBody();
		String blockCode = oneBlockCodeRes.getCreditCard() != null ? oneBlockCodeRes.getCreditCard().getBlockCode()
				: ProductsExpServiceConstant.EMPTY;
		ResponseEntity<FetchCardResponse> getCardRes = creditCardClient.getCreditCardDetails(correlationId,
				accountId);
		if (getCardRes != null && getCardRes.getStatusCode() == HttpStatus.OK
				&& getCardRes.getBody().getStatus().getStatusCode() == ProductsExpServiceConstant.ZERO) {
			FetchCardResponse oneGetCardRes = getCardRes.getBody();
			String cardId = oneGetCardRes.getCreditCard() != null ? oneGetCardRes.getCreditCard().getCardId()
					: ProductsExpServiceConstant.EMPTY;
			String expiredBy = oneGetCardRes.getCreditCard() != null
					? oneGetCardRes.getCreditCard().getCardInfo().getExpiredBy()
					: ProductsExpServiceConstant.EMPTY;
			response.setBlockCode(blockCode);
			response.setCreditCardRefId(cardId);
			response.setExpiryDate(expiredBy);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
			oneServiceResponse.setData(response);
			return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
		} else {
			return this.handlingFailedResponse(oneServiceResponse, responseHeaders);
		}

	}

	/**
	 * Description:- handling fail response
	 *
	 * @param oneServiceResponse
	 * @param responseHeaders
	 *
	 */
	public ResponseEntity<TmbOneServiceResponse<VerifyCreditCardResponse>> handlingFailedResponse(
			TmbOneServiceResponse<VerifyCreditCardResponse> oneServiceResponse, HttpHeaders responseHeaders) {

		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
				ResponseCode.FAILED.getService()));
		return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
	}
}