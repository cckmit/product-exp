package com.tmb.oneapp.productsexpservice.controller;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CRMID;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CashForUConfigInfo;
import com.tmb.common.model.MinMaxAmount;
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
			@ApiImplicitParam(name = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
			@ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000018593707", required = true, dataType = "string", paramType = "header") })
	public ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> getLoanEligibleDetail(
			@ApiParam(hidden = true) @RequestHeader Map<String, String> headers,
			@RequestBody EligibleLeadRequest requestBody) {
		String correlationId = headers.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<EligibleLeadResponse> serviceResponse = new TmbOneServiceResponse<>();
		EligibleLeadResponse loanDetails = null;
		ResponseEntity<TmbOneServiceResponse<CashForUConfigInfo>> responseConfig = creditCardClient
				.getCurrentCashForYouRate();
		String productId = requestBody.getProductId();
		try {
			String groupAccountId = requestBody.getGroupAccountId();
			String disbursementDate = requestBody.getDisbursementDate();
			

			if (!Strings.isNullOrEmpty(groupAccountId) && !Strings.isNullOrEmpty(disbursementDate)) {
				long startTime = System.currentTimeMillis();
				ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> loanResponse = creditCardClient
						.getEligibleLeads(correlationId, requestBody);
				loanDetails = loanResponse.getBody().getData();
				long endTime = System.currentTimeMillis();
				logger.info("/loan/get-eligible-lead Execution Time : " + (endTime - startTime));
				
				processSetDefaultMinimunMaximumAmounts(productId, loanDetails, responseConfig, true);
				
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
			processSetDefaultMinimunMaximumAmounts(productId, loanDetails, responseConfig, false);
			serviceResponse.setData(loanDetails);
		}
		return getTmbOneServiceResponseResponseEntity(responseHeaders, serviceResponse);
	}

	private void processSetDefaultMinimunMaximumAmounts(String productId, EligibleLeadResponse loanDetails,
			ResponseEntity<TmbOneServiceResponse<CashForUConfigInfo>> responseConfig, boolean isInULDX) {
		CashForUConfigInfo cashForUConfigInfo = responseConfig.getBody().getData();
		MinMaxAmount minMaxAmount = new MinMaxAmount();
		if (cashForUConfigInfo != null) {
			List<MinMaxAmount> minMaxAmounts = cashForUConfigInfo.getMinMaxAmounts().stream()
					.filter(minMaxAmountsConfig -> productId.equalsIgnoreCase(minMaxAmountsConfig.getProductId()))
					.collect(Collectors.toList());
			if (minMaxAmounts.isEmpty()) {
				List<MinMaxAmount> othersAmounts = cashForUConfigInfo.getMinMaxAmounts().stream()
						.filter(minMaxAmountsConfig -> "Others".equalsIgnoreCase(minMaxAmountsConfig.getProductId()))
						.collect(Collectors.toList());
				minMaxAmount = othersAmounts.get(0);
			} else {
				minMaxAmount = minMaxAmounts.get(0);
			}
			
			if (isInULDX) {
				loanDetails.setMinimumAmount(minMaxAmount.getMinInUldxLead());
				loanDetails.setMaximumAmount(minMaxAmount.getMaxInUldxLead());
			} else {
				loanDetails.setMinimumAmount(minMaxAmount.getMinNotInUldxLead());
				loanDetails.setMaximumAmount(minMaxAmount.getMaxNotInUldxLead());
			}
		} else {
			logger.info("Not found cashForUConfigInfo");
			loanDetails.setMinimumAmount("5,000");
			loanDetails.setMaximumAmount("500,000");
		}
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
