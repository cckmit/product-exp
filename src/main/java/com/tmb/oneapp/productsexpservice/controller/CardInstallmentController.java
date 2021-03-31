
package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.model.blockcard.Status;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CardInstallmentResponse;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CardInstallmentQuery;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import io.swagger.annotations.Api;
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

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * @author Admin
 */
@RestController
@Api(tags = "Card Installment Api")
public class CardInstallmentController {
	private static final TMBLogger<CardInstallmentController> logger = new TMBLogger<>(CardInstallmentController.class);
	private final CreditCardClient creditCardClient;
	private final CreditCardLogService creditCardLogService;


	/**
	 * Constructor
	 *  @param
	 * @param creditCardClient
	 * @param creditCardLogService
	 */

	@Autowired
	public CardInstallmentController(CreditCardClient creditCardClient, CreditCardLogService creditCardLogService) {
		this.creditCardClient = creditCardClient;
		this.creditCardLogService = creditCardLogService;
	}

	/**
	 * campaign transaction api
	 *
	 * @param requestBodyParameter
	 * @return campaign transaction response
	 */


	@LogAround
	@ApiOperation(value = "Campaign Transactions Api")
	@PostMapping(value = "/creditcard/card-installment-confirm")
	public ResponseEntity<TmbOneServiceResponse<List<CardInstallmentResponse>>> confirmCardInstallment(
			@ApiParam(value = "Correlation ID", defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true) @RequestHeader("X-Correlation-ID") String correlationId,
			@RequestBody CardInstallmentQuery requestBodyParameter, @RequestHeader Map<String, String> requestHeadersParameter)
			throws TMBCommonException {
		logger.info("Get Campaign Transactions request body parameter: {}", requestBodyParameter);
		String activityId = ProductsExpServiceConstant.APPLY_SO_GOOD_ON_CLICK_CONFIRM_BUTTON;
		String activityDate = Long.toString(System.currentTimeMillis());

		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, activityDate, activityId);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

		TmbOneServiceResponse<List<CardInstallmentResponse>> oneServiceResponse = new TmbOneServiceResponse<>();
		try {
            String accountId = requestBodyParameter.getAccountId();

			if (accountId!=null)
			{
				ResponseEntity<TmbOneServiceResponse<List<CardInstallmentResponse> >> cardInstallment = creditCardClient.confirmCardInstallment(correlationId, requestBodyParameter);
				TmbOneServiceResponse<List<CardInstallmentResponse>> cardInstallmentResp = cardInstallment.getBody();
				creditCardLogService.applySoGoodConfirmEvent(correlationId,requestHeadersParameter,requestBodyParameter);
				if (cardInstallmentResp != null) {

					Status status = new Status();
					status.setStatusCode(cardInstallment.getBody().getStatus().getCode());

					List<CardInstallmentResponse> data = cardInstallmentResp.getData();

					if (data!=null) {

						creditCardLogService.logActivity(creditCardEvent);

						oneServiceResponse.setData(cardInstallmentResp.getData());
						oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
								ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
					} else {
						creditCardEvent.setActivityStatus(ProductsExpServiceConstant.FAILURE);


						creditCardEvent.setResult(ProductsExpServiceConstant.FAILURE);
						creditCardLogService.logActivity(creditCardEvent);
						oneServiceResponse.setData(cardInstallmentResp.getData());
						oneServiceResponse.setStatus(
								new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(), ResponseCode.GENERAL_ERROR.getMessage(),
										ResponseCode.GENERAL_ERROR.getService(), ResponseCode.GENERAL_ERROR.getDesc()));
						return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
					}
				}
			} else {
				creditCardEvent.setActivityStatus(ProductsExpServiceConstant.FAILURE);
				creditCardEvent.setResult(ProductsExpServiceConstant.FAILURE);
				creditCardEvent.setFailReason(ResponseCode.DATA_NOT_FOUND_ERROR.getMessage());
				creditCardLogService.logActivity(creditCardEvent);

				oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
						ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
						ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
				return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
			}
		} catch (Exception e) {
			creditCardEvent.setActivityStatus(ProductsExpServiceConstant.FAILURE);
			creditCardEvent.setResult(ProductsExpServiceConstant.FAILURE);
			creditCardEvent.setFailReason(e.getMessage());
			creditCardLogService.logActivity(creditCardEvent);

			logger.error("Error while getBlockCardDetails: {}", e);
			throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), HttpStatus.OK, null);
		}

		return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);

	}

}