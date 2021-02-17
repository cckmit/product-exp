package com.tmb.oneapp.productsexpservice.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitReq;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.SetCreditLimitResp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * SetCreditLimitController request mapping will handle apis call and then
 * navigate to respective method
 *
 */
@RestController
@Api(tags = "Temporary and Permanent Credit Card Limit")
public class SetCreditLimitController {
	private final CreditCardClient creditCardClient;
	private static final TMBLogger<SetCreditLimitController> logger = new TMBLogger<>(SetCreditLimitController.class);

	/**
	 * Constructor
	 * 
	 * @param creditCardClient
	 */
	@Autowired
	public SetCreditLimitController(CreditCardClient creditCardClient) {
		this.creditCardClient = creditCardClient;
	}

	/**
	 * Temporary and Permanent Credit Card Limit api
	 * 
	 * @param requestBodyParameter
	 * @param correlationId
	 * @return status code
	 */
	@LogAround
	@ApiOperation(value = "Temporary and Permanent Credit Card Limit")
	@PostMapping(value = "/credit-card/set-credit-limit")
	@ApiImplicitParams({
			@ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da") })

	public ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> setCreditLimit(
			@RequestBody SetCreditLimitReq requestBodyParameter,
			@RequestHeader(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID, required = true) final String correlationId) {
		logger.info("Request Parameter fetchCreditLimit : {} ", requestBodyParameter);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<SetCreditLimitResp> oneServiceResponse = new TmbOneServiceResponse<>();
		try {
			ResponseEntity<TmbOneServiceResponse<SetCreditLimitResp>> response = creditCardClient
					.fetchSetCreditLimit(correlationId, requestBodyParameter);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
			oneServiceResponse.setData(response.getBody().getData());
			return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
		} catch (Exception ex) {
			logger.error("Unable to fetch set credit limit response: {}", ex);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
					ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));

			return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
		}

	}

}
