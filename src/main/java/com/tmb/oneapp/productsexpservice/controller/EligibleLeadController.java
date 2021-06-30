package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.loan.EligibleLeadRequest;
import com.tmb.oneapp.productsexpservice.model.loan.EligibleLeadResponse;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentPromotion;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CORRELATION_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CRMID;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "Credit Card-Cash For You")
public class EligibleLeadController {
	private static final TMBLogger<EligibleLeadController> logger = new TMBLogger<>(EligibleLeadController.class);

	private final CreditCardClient creditCardClient;

	@Autowired
	public EligibleLeadController(CreditCardClient creditCardClient) {
		this.creditCardClient = creditCardClient;
	}

	/**
	 * @param correlationId
	 * @param requestBody
	 * @return
	 */
	@LogAround
	@PostMapping(value = "/loan/get-eligible-lead", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "get account eligible lead details")
	@ApiImplicitParams({
			@ApiImplicitParam(name = X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
			@ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000018593707", required = true, dataType = "string", paramType = "header") })
	public ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> getLoanEligibleDetail(
			@ApiParam(hidden = true) @RequestHeader Map<String, String> headers,
			@RequestBody EligibleLeadRequest requestBody) {
		String correlationId = headers.get(ProductsExpServiceConstant.X_CORRELATION_ID);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<EligibleLeadResponse> serviceResponse = new TmbOneServiceResponse<>();
		EligibleLeadResponse loanDetails = null;
		try {
			String groupAccountId = requestBody.getGroupAccountId();
			String disbursementDate = requestBody.getDisbursementDate();

			if (!Strings.isNullOrEmpty(groupAccountId) && !Strings.isNullOrEmpty(disbursementDate)) {
				ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> loanResponse = creditCardClient
						.getEligibleLeads(correlationId, requestBody);
				loanDetails = loanResponse.getBody().getData();

				serviceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
						ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
				serviceResponse.setData(loanDetails);
				return ResponseEntity.ok().headers(responseHeaders).body(serviceResponse);
			}
		} catch (Exception e) {
			loanDetails = new EligibleLeadResponse();
			InstallmentPromotion promotionInfo = new InstallmentPromotion();
			promotionInfo.setCashChillChillFlagAllow("N");
			promotionInfo.setCashTransferFlagAllow("N");
			promotionInfo.setGroupAccountId(requestBody.getGroupAccountId());
			List<InstallmentPromotion> handleNoneLeadList = new ArrayList<>();
			handleNoneLeadList.add(promotionInfo);
			loanDetails.setInstallmentPromotions(handleNoneLeadList);
			serviceResponse.setData(loanDetails);
		}
		return getTmbOneServiceResponseResponseEntity(responseHeaders, serviceResponse);
	}

	ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> getFailedResponse(HttpHeaders responseHeaders,
			TmbOneServiceResponse<EligibleLeadResponse> serviceResponse, Exception e) {
		logger.error("Error while getting eligible lead controller: {}", e);
		serviceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
				ResponseCode.FAILED.getService()));
		return ResponseEntity.badRequest().headers(responseHeaders).body(serviceResponse);
	}

	/**
	 * @param responseHeaders
	 * @param serviceResponse
	 * @return
	 */
	private ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> getTmbOneServiceResponseResponseEntity(
			HttpHeaders responseHeaders, TmbOneServiceResponse<EligibleLeadResponse> serviceResponse) {
		serviceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
				ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
				ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
		return ResponseEntity.badRequest().headers(responseHeaders).body(serviceResponse);
	}
}
