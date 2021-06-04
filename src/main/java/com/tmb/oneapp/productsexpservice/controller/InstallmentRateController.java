package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.loan.CashForYourResponse;
import com.tmb.oneapp.productsexpservice.model.loan.EnquiryInstallmentRequest;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateRequest;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateResponse;
import com.tmb.oneapp.productsexpservice.service.CashForUService;
import com.tmb.oneapp.productsexpservice.service.InstallmentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CORRELATION_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CRMID;

import java.time.Instant;
import java.util.Objects;

@RestController
@Api(tags = "Credit Card-Cash For You")
public class InstallmentRateController {
	private static final TMBLogger<InstallmentRateController> logger = new TMBLogger<>(InstallmentRateController.class);
	private final CreditCardClient creditCardClient;
	private final CashForUService cashForService;

	@Autowired
	public InstallmentRateController(CreditCardClient creditCardClient, CashForUService cashForService) {
		this.creditCardClient = creditCardClient;
		this.cashForService = cashForService;
	}

	/**
	 * @param correlationId
	 * @param requestBody
	 * @return
	 */
	@LogAround
	@PostMapping(value = "/credit-card/get-installment-rate", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "get account installment details")
	@ApiImplicitParams({
			@ApiImplicitParam(name = X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
			@ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000018593707", required = true, dataType = "string", paramType = "header") })
	public ResponseEntity<TmbOneServiceResponse<CashForYourResponse>> getInstallmentAccountDetail(
			@RequestHeader String correlationId,
			@ApiParam(value = "Account ID , start date, end date", defaultValue = "00016109738001", required = true) @RequestBody EnquiryInstallmentRequest requestBody) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<CashForYourResponse> oneServiceResponse = new TmbOneServiceResponse<>();

		try {
			InstallmentRateRequest rateRequest = constructInstallmentRequest(requestBody);
			ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> loanResponse = creditCardClient
					.getInstallmentRate(correlationId, rateRequest);

			if (loanResponse.getStatusCode() == HttpStatus.OK) {
				CashForYourResponse cashForYouInfo = cashForService
						.calculateInstallmentForCashForYou(loanResponse.getBody().getData(),rateRequest.getCashChillChillFlag(),rateRequest.getCashTransferFlag(),correlationId,requestBody);
				oneServiceResponse.setData(cashForYouInfo);
				oneServiceResponse
						.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
								ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
			} else {
				if (Objects.isNull(loanResponse.getBody())) {
					oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
							ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(),
							ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
							ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
				} else {
					oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(),
							ResponseCode.FAILED.getMessage(), ResponseCode.FAILED.getService()));
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting installment rate controller: {}", e);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService()));
		}

		return ResponseEntity.ok(oneServiceResponse);

	}

	/**
	 * Construct installment request
	 * 
	 * @param requestBody
	 * @return
	 */
	private InstallmentRateRequest constructInstallmentRequest(EnquiryInstallmentRequest requestBody) {
		InstallmentRateRequest rateReq = new InstallmentRateRequest();
		rateReq.setAmount(requestBody.getAmount());
		rateReq.setBillCycleCutDate(requestBody.getBillCycleCutDate());
		rateReq.setCashChillChillFlag(requestBody.getCashChillChillFlag());
		rateReq.setCashTransferFlag(requestBody.getCashTransferFlag());
		rateReq.setDisbursementDate(requestBody.getDisbursementDate());
		rateReq.setGetAllDetailFlag(requestBody.getGetAllDetailFlag());
		rateReq.setGroupAccountId(requestBody.getAccountId().substring(9, requestBody.getAccountId().length()));
		rateReq.setPromoSegment(requestBody.getPromoSegment());
		return rateReq;
	}
}
