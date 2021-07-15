package com.tmb.oneapp.productsexpservice.controller;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CRMID;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.customer.UpdateEStatmentRequest;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.applyestatement.ApplyEStatementResponse;
import com.tmb.oneapp.productsexpservice.service.ApplyEStatementService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(tags = "Apply eStatement")
public class ApplyEStatementController {
	private static final TMBLogger<ApplyEStatementController> logger = new TMBLogger<>(ApplyEStatementController.class);
	private final ApplyEStatementService applyEStatementService;

	@Autowired
	public ApplyEStatementController(ApplyEStatementService applyEStatementService) {
		this.applyEStatementService = applyEStatementService;
	}

	/**
	 * @param correlationId
	 * @param crmid
	 * @return
	 */
	@LogAround
	@PostMapping(value = "/credit-card/get-e-statement", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "get e-statement")
	@ApiImplicitParams({
			@ApiImplicitParam(name = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
			@ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000012004011", required = true, dataType = "string", paramType = "header") })
	public ResponseEntity<TmbOneServiceResponse<ApplyEStatementResponse>> getEStatement(
			@ApiParam(hidden = true) @RequestHeader Map<String, String> headers) {
		String correlationId = headers.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String crmId = headers.get(ProductsExpServiceConstant.X_CRMID);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<ApplyEStatementResponse> oneServiceResponse = new TmbOneServiceResponse<>();

		try {
			ApplyEStatementResponse applyEStatementResponse = applyEStatementService.getEStatement(crmId,
					correlationId);
			logger.info("ApplyEStatementResponse while getting e-statement: {}", applyEStatementResponse.toString());
			oneServiceResponse.setData(applyEStatementResponse);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		} catch (Exception e) {
			logger.error("Error while getting e-statement: {}", e);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(),e.toString()));
		}
		return ResponseEntity.ok(oneServiceResponse);
	}

	@LogAround
	@PostMapping(value = "/credit-card/update-e-statement", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "update e-statement")
	@ApiImplicitParams({
			@ApiImplicitParam(name = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
			@ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000012004011", required = true, dataType = "string", paramType = "header") })
	public ResponseEntity<TmbOneServiceResponse<ApplyEStatementResponse>> getUpdateEStatement(
			@ApiParam(hidden = true) @RequestHeader Map<String, String> headers,
			@RequestBody UpdateEStatmentRequest updateEstatementReq) {
		String correlationId = headers.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String crmId = headers.get(ProductsExpServiceConstant.X_CRMID);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<ApplyEStatementResponse> oneServiceResponse = new TmbOneServiceResponse<>();
		try {
			logger.info("Enable ApplyEStatementResponse for : {}", crmId);
			applyEStatementService.updateEstatement(crmId, correlationId, updateEstatementReq);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));

		} catch (Exception e) {
			logger.error("Error while getting e-statement: {}", e);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(),e.toString()));
		}
		return ResponseEntity.ok(oneServiceResponse);
	}

}
