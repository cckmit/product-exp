package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.loan.DepositRequest;
import com.tmb.oneapp.productsexpservice.model.loan.DepositResponse;
import com.tmb.oneapp.productsexpservice.util.InternalRespUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Objects;

@RestController
@Api(tags = "Fetch Home loan account statement")
public class MoneyTransferController {
	private static final TMBLogger<MoneyTransferController> logger = new TMBLogger<>(MoneyTransferController.class);
	@Autowired
	CreditCardClient creditCardClient;

	public MoneyTransferController(CreditCardClient creditCardClient) {
		this.creditCardClient = creditCardClient;
	}

	@SuppressWarnings("unchecked")
	@LogAround
	@PostMapping(value = "/card-money-transfer", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Card money Transfer")
	@ApiImplicitParams({
			@ApiImplicitParam(name = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da") })

	public ResponseEntity<TmbOneServiceResponse<DepositResponse>> cardMoneyTransfer(
			@RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
			@RequestBody DepositRequest requestBody) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<DepositResponse> serviceResponse = new TmbOneServiceResponse<>();

		try {

			String toAccountId = requestBody.getDeposit().getFromAccountId();
			String amounts = requestBody.getDeposit().getAmounts();
			String expiredDate = requestBody.getDeposit().getExpiredDate();
			String modelType = requestBody.getDeposit().getModelType();
			String fromAccountId = requestBody.getDeposit().getFromAccountId();
			String referenceCode = requestBody.getDeposit().getReferenceCode();
			String fromAccountType = requestBody.getDeposit().getFromAccountType();
			String transferredDate = requestBody.getDeposit().getTransferredDate();
			String waiverCode = requestBody.getDeposit().getWaiverCode();
			String toAccountType = requestBody.getDeposit().getToAccountType();
			if (!Strings.isNullOrEmpty(toAccountId) && !Strings.isNullOrEmpty(amounts)
					&& !Strings.isNullOrEmpty(expiredDate) && !Strings.isNullOrEmpty(modelType)
					&& !Strings.isNullOrEmpty(fromAccountId) && !Strings.isNullOrEmpty(referenceCode)
					&& !Strings.isNullOrEmpty(fromAccountType) && !Strings.isNullOrEmpty(transferredDate)
					&& !Strings.isNullOrEmpty(waiverCode) && !Strings.isNullOrEmpty(toAccountType)) {
				ResponseEntity<TmbOneServiceResponse<DepositResponse>> tmbOneServiceResponse = creditCardClient
						.cardMoneyTransfer(correlationId, requestBody);
				TmbOneServiceResponse<DepositResponse> response = tmbOneServiceResponse.getBody();

				if (Objects.nonNull(response.getData())) {
					serviceResponse.setData(response.getData());
				}

				int statusCodeValue = tmbOneServiceResponse.getStatusCodeValue();
				HttpStatus statusCode = tmbOneServiceResponse.getStatusCode();

				if (response != null && statusCodeValue == 200 && statusCode == HttpStatus.OK) {
					serviceResponse
							.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
									ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
					return ResponseEntity.ok().headers(responseHeaders).body(serviceResponse);
				} else {
					return (ResponseEntity<TmbOneServiceResponse<DepositResponse>>) InternalRespUtil
							.generatedResponseFromService(responseHeaders, serviceResponse, response.getStatus());

				}
			} else {
				return getTmbOneServiceResponseResponseEntity(responseHeaders, serviceResponse);
			}

		} catch (Exception e) {
			return failedErrorResponse(responseHeaders, serviceResponse, e);
		}

	}

	ResponseEntity<TmbOneServiceResponse<DepositResponse>> failedErrorResponse(HttpHeaders responseHeaders,
			TmbOneServiceResponse<DepositResponse> serviceResponse, Exception e) {
		logger.error("Error while getting LoanAccountStatement: {}", e);
		serviceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
				ResponseCode.FAILED.getService()));
		return ResponseEntity.badRequest().headers(responseHeaders).body(serviceResponse);
	}

	/**
	 * @param responseHeaders
	 * @param serviceResponse
	 * @return
	 */
	ResponseEntity<TmbOneServiceResponse<DepositResponse>> getTmbOneServiceResponseResponseEntity(
			HttpHeaders responseHeaders, TmbOneServiceResponse<DepositResponse> serviceResponse) {
		serviceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
				ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
				ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
		return ResponseEntity.badRequest().headers(responseHeaders).body(serviceResponse);
	}

}
