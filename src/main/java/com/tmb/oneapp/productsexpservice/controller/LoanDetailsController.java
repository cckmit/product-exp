package com.tmb.oneapp.productsexpservice.controller;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CRMID;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.creditcard.UpdateEStatmentResp;
import com.tmb.common.model.loan.InstantLoanCreationRequest;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CardEmail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.EStatementDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.loan.AccountId;
import com.tmb.oneapp.productsexpservice.model.loan.HomeLoanFullInfoResponse;
import com.tmb.oneapp.productsexpservice.model.loan.Payment;
import com.tmb.oneapp.productsexpservice.model.loan.Rates;
import com.tmb.oneapp.productsexpservice.service.ApplyEStatementService;
import com.tmb.oneapp.productsexpservice.service.InstantLoanService;
import com.tmb.oneapp.productsexpservice.util.ConversionUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(tags = "Loan detail controller")
public class LoanDetailsController {
	private static final TMBLogger<LoanDetailsController> log = new TMBLogger<>(LoanDetailsController.class);
	private final AccountRequestClient accountRequestClient;
	private final CommonServiceClient commonServiceClient;
	private final CustomerServiceClient customerServiceClient;
	private final ApplyEStatementService applyEStatementService;
	private final InstantLoanService instanceLoanService;

	/**
	 * Constructor
	 *
	 * @param accountRequestClient
	 * @param commonServiceClient
	 * @param creditCardLogService
	 */
	@Autowired
	public LoanDetailsController(AccountRequestClient accountRequestClient, CommonServiceClient commonServiceClient,
			CustomerServiceClient customerServiceClient, ApplyEStatementService applyEStatementService,
			InstantLoanService instantLoanService) {
		this.accountRequestClient = accountRequestClient;
		this.commonServiceClient = commonServiceClient;
		this.customerServiceClient = customerServiceClient;
		this.applyEStatementService = applyEStatementService;
		this.instanceLoanService = instantLoanService;
	}

	/**
	 * @param requestHeadersParameter
	 * @param requestBody
	 * @return
	 */
	@LogAround
	@PostMapping(value = "/loan/get-account-detail", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "get loan account detail")
	@ApiImplicitParams({
			@ApiImplicitParam(name = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
			@ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000018593707", required = true, dataType = "string", paramType = "header") })
	public ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> getLoanAccountDetail(
			@ApiParam(hidden = true) @RequestHeader Map<String, String> requestHeadersParameter,
			@ApiParam(value = "Account ID", defaultValue = "00016109738001", required = true) @RequestBody AccountId requestBody) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<HomeLoanFullInfoResponse> oneServiceResponse = new TmbOneServiceResponse<>();

		String correlationId = requestHeadersParameter.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String crmId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CRMID);

		try {
			String accountId = requestBody.getAccountNo();
			if (!Strings.isNullOrEmpty(accountId)) {
				ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> loanResponse = accountRequestClient
						.getLoanAccountDetail(correlationId, requestBody);
				int statusCodeValue = loanResponse.getStatusCodeValue();
				HttpStatus statusCode = loanResponse.getStatusCode();

				if (loanResponse.getBody() != null && statusCodeValue == 200 && statusCode == HttpStatus.OK) {

					return getTmbOneServiceResponseResponseEntity(responseHeaders, oneServiceResponse, crmId,
							correlationId, loanResponse);
				} else {
					return getFailedResponse(responseHeaders, oneServiceResponse);

				}
			} else {
				return getFailedResponse(responseHeaders, oneServiceResponse);
			}

		} catch (Exception e) {
			log.error("Error while getLoanAccountDetails: {}", e);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService()));
			return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
		}

	}

	ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> getTmbOneServiceResponseResponseEntity(
			HttpHeaders responseHeaders, TmbOneServiceResponse<HomeLoanFullInfoResponse> oneServiceResponse,
			String crmId, String correlationId,
			ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> loanResponse) {
		HomeLoanFullInfoResponse loanDetails = loanResponse.getBody().getData();
		String productId = loanResponse.getBody().getData().getAccount().getProductId();
		Rates rates = loanResponse.getBody().getData().getAccount().getRates();
		Double currentInterestRate = ConversionUtil.stringToDouble(rates.getCurrentInterestRate());
		Double originalInterestRate = ConversionUtil.stringToDouble(rates.getOriginalInterestRate());
		String monthlyPaymentAmount = loanDetails.getAccount().getPayment().getMonthlyPaymentAmount();
		Double monthlyPayment = ConversionUtil.stringToDouble(monthlyPaymentAmount);
		DecimalFormat df = new DecimalFormat("#,###,##0.00");
		Payment payment = loanDetails.getAccount().getPayment();

		String formattedPayment = df.format(monthlyPayment);
		payment.setMonthlyPaymentAmount(formattedPayment);
		DecimalFormat threeDecimalPlaces = new DecimalFormat("#.00");
		String currentInterest = threeDecimalPlaces.format(currentInterestRate);
		String originalInterest = threeDecimalPlaces.format(originalInterestRate);
		String currentInterestRateInPercent = currentInterest.concat(" %");
		String originalInterestRateInPercent = originalInterest.concat(" %");
		rates.setCurrentInterestRate(currentInterestRateInPercent);
		rates.setOriginalInterestRate(originalInterestRateInPercent);
		ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> fetchProductConfigList = commonServiceClient
				.getProductConfig(correlationId);

		List<ProductConfig> list = fetchProductConfigList.getBody().getData();
		Iterator<ProductConfig> iterator = list.iterator();
		while (iterator.hasNext()) {
			ProductConfig productConfig = iterator.next();
			if (productConfig.getProductCode().equalsIgnoreCase(productId)) {
				loanDetails.setProductConfig(productConfig);
			}
		}
		processSetEStatementDetail(loanDetails, crmId, correlationId);
		processSetAccountID(loanDetails);

		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		oneServiceResponse.setData(loanDetails);
		return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
	}

	private void processSetAccountID(HomeLoanFullInfoResponse loanDetails) {
		if (loanDetails.getAccount() != null) {
			if (loanDetails.getAccount().getDirectDebit() != null
					&& !"01".equals(loanDetails.getAccount().getDirectDebit().getAffiliateSequenceNo())
					&& !"1".equals(loanDetails.getAccount().getDirectDebit().getSequenceNo())) {
				loanDetails.getAccount().getDirectDebit().setAccountId("");
			}
		}
	}

	private void processSetEStatementDetail(HomeLoanFullInfoResponse loanDetails, String crmId, String correlationId) {
		EStatementDetail result = new EStatementDetail();
		CardEmail cardEmail = new CardEmail();
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> responseWorkingProfileInfo = customerServiceClient
				.getCustomerProfile(crmId);
		CustGeneralProfileResponse profileResponse = responseWorkingProfileInfo.getBody().getData();
		if (profileResponse != null) {
			result.setEmailAddress(profileResponse.getEmailAddress());
			result.setEmailVerifyFlag(profileResponse.getEmailVerifyFlag());
			cardEmail.setEmailAddress(profileResponse.getEmailAddress());
		}
		UpdateEStatmentResp applyEStatementResponse = applyEStatementService.getEStatement(crmId, correlationId);
		if (applyEStatementResponse != null) {
			cardEmail.setEmaileStatementFlag(
					applyEStatementResponse.getCustomer().getStatementFlag().getECashToGoStatementFlag());
		}
		loanDetails.setCardEmail(cardEmail);
		loanDetails.setEstatementDetail(result);
	}

	@LogAround
	@PostMapping(value = "/loan/activate-instanceloan", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Process for activate instance loan application")
	@ApiImplicitParams({
			@ApiImplicitParam(name = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
			@ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000018593707", required = true, dataType = "string", paramType = "header") })
	public ResponseEntity<TmbOneServiceResponse<Object>> createInstantLoanApplication(
			@ApiParam(hidden = true) @RequestHeader Map<String, String> reqHeaders,
			@RequestBody InstantLoanCreationRequest request) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<Object> oneServiceResponse = new TmbOneServiceResponse<>();
		try {
			log.info("== Create activate instance loan request "
					+ TMBUtils.getObjectMapper().writeValueAsString(request));
			instanceLoanService.createInstanceLoanApplication(reqHeaders, request, oneServiceResponse);

			if (ResponseCode.SUCESS.getCode().equals(oneServiceResponse.getStatus().getCode())) {
				return ResponseEntity.ok(oneServiceResponse);
			} else {
				return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
			}
		} catch (Exception e) {
			log.error("Error while createInstantLoanApplication: {}", e);
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService()));
			return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
		}
	}

	ResponseEntity<TmbOneServiceResponse<HomeLoanFullInfoResponse>> getFailedResponse(HttpHeaders responseHeaders,
			TmbOneServiceResponse<HomeLoanFullInfoResponse> oneServiceResponse) {
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
				ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
				ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
		return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
	}

}
