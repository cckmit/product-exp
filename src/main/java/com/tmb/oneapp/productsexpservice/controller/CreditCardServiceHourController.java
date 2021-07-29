package com.tmb.oneapp.productsexpservice.controller;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CRMID;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.CreditCardServiceHour;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(tags = "Credit card service hour")
public class CreditCardServiceHourController {
	private static final TMBLogger<CreditCardServiceHourController> logger = new TMBLogger<>(
			CreditCardServiceHourController.class);
	private final CreditCardClient creditCardClient;

	@Autowired
	public CreditCardServiceHourController(CreditCardClient creditCardClient) {
		this.creditCardClient = creditCardClient;
	}

	/**
	 * @param correlationId
	 * @param crmid
	 * @return
	 */
	@LogAround
	@PostMapping(value = "/credit-card/config/servicehour", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "get credit card service hour")
	@ApiImplicitParams({
			@ApiImplicitParam(name = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
			@ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000012004011", required = true, dataType = "string", paramType = "header") })
	public ResponseEntity<TmbOneServiceResponse<CreditCardServiceHour>> getCreditCardServiceHour(
			@ApiParam(hidden = true) @RequestHeader Map<String, String> headers) {
		TmbOneServiceResponse<CreditCardServiceHour> oneServiceResponse = new TmbOneServiceResponse<>();
		CreditCardServiceHour creditCardServiceHour = new CreditCardServiceHour();
		try {
			ResponseEntity<TmbOneServiceResponse<CreditCardServiceHour>> response = creditCardClient
					.getCreditCardServiceHour();
			if (response != null && response.getStatusCode() == HttpStatus.OK) {
				creditCardServiceHour = response.getBody().getData();
				logger.info("CreditCardServiceHourResponse while getting credit card service hour: {}",
						creditCardServiceHour.toString());
				oneServiceResponse.setData(creditCardServiceHour);
				oneServiceResponse
						.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
								ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
				return ResponseEntity.ok(oneServiceResponse);
			} else {
				logger.error("Getting credit card service hour: data not found");
				oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
						ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
						ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
				return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
			}
		} catch (Exception e) {
			logger.error("Error while getting credit card service hour: {}", e);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), e.toString()));
			return ResponseEntity.badRequest().body(oneServiceResponse);
		}
	}

}
