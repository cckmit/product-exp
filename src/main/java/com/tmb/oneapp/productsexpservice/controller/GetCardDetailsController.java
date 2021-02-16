package com.tmb.oneapp.productsexpservice.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.GetCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.GetCreditCardDetailsReq;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * GetCardDetailsController request mapping will handle apis call and then
 * navigate to respective method
 *
 */
@RestController
@Api(tags = "Fetch credit card details")
public class GetCardDetailsController {
	private final CreditCardClient creditCardClient;
	private static final TMBLogger<GetCardDetailsController> logger = new TMBLogger<>(GetCardDetailsController.class);

	/**
	 * Constructor
	 * 
	 * @param creditCardClient
	 */
	@Autowired
	public GetCardDetailsController(CreditCardClient creditCardClient) {
		super();
		this.creditCardClient = creditCardClient;
	}

	/**
	 * fetch credit card details
	 * 
	 * @param request
	 * @return card details
	 */

	@LogAround
	@PostMapping(value = "/credit-card/fetch-card-details")
	@ApiOperation(value = "Fetch credit card details")
	@ApiImplicitParams({
			@ApiImplicitParam(name = ProductsExpServiceConstant.HEADER_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da") })
	public ResponseEntity<TmbOneServiceResponse<GetCardResponse>> fetchCardDetails(
			@RequestBody GetCreditCardDetailsReq request,
			@RequestHeader(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID, required = true) final String correlationId) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<GetCardResponse> oneServiceResponse = new TmbOneServiceResponse<GetCardResponse>();
		try {
			String accountId = request.getAccountId();
			if (!Strings.isNullOrEmpty(accountId)) {
				ResponseEntity<GetCardResponse> getCardRes = creditCardClient.getCreditCardDetails(correlationId,
						accountId);
				oneServiceResponse
						.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
								ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
				oneServiceResponse.setData(getCardRes.getBody());
				return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
			} else {
				oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
						ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
						ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
				return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
			}
		} catch (Exception e) {
			logger.error("Unable to fetch CardDetails : {}", e);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
					ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));

			return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
		}

	}

}
