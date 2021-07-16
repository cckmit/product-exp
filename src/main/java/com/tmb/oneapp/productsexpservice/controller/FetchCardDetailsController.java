package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.EStatementDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCreditCardDetailsReq;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductCodeData;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.model.applyestatement.ApplyEStatementResponse;
import com.tmb.oneapp.productsexpservice.service.ApplyEStatementService;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CRMID;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * GetCardDetailsController request mapping will handle apis call and then
 * navigate to respective method
 */
@RestController
@Api(tags = "Fetch credit card details")
public class FetchCardDetailsController {
	private final CreditCardClient creditCardClient;
	private final CommonServiceClient commonServiceClient;
	private final CreditCardLogService creditCardLogService;
	private final CustomerServiceClient customerServiceClient;
	private final ApplyEStatementService applyEStatementService;
	private static final TMBLogger<FetchCardDetailsController> logger = new TMBLogger<>(
			FetchCardDetailsController.class);

	/**
	 * Constructor
	 *
	 * @param creditCardClient
	 * @param creditCardLogService
	 */
	@Autowired
	public FetchCardDetailsController(CreditCardClient creditCardClient, CommonServiceClient commonServiceClient,
			CreditCardLogService creditCardLogService, CustomerServiceClient customerServiceClient,
			ApplyEStatementService applyEStatementService) {
		super();
		this.creditCardClient = creditCardClient;
		this.commonServiceClient = commonServiceClient;
		this.creditCardLogService = creditCardLogService;
		this.customerServiceClient = customerServiceClient;
		this.applyEStatementService = applyEStatementService;
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
			@ApiImplicitParam(name = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da"),
			@ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000018593707", required = true, dataType = "string", paramType = "header") })
	public ResponseEntity<TmbOneServiceResponse<FetchCardResponse>> fetchCardDetails(
			@RequestBody FetchCreditCardDetailsReq request,
			@ApiParam(hidden = true) @RequestHeader Map<String, String> requestHeadersParameter) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		TmbOneServiceResponse<FetchCardResponse> oneServiceResponse = new TmbOneServiceResponse<>();
		String correlationId = requestHeadersParameter.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		String crmId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CRMID);
		String activityDate = Long.toString(System.currentTimeMillis());
		String activityId = ProductsExpServiceConstant.ACTIVITY_ID_LOAD_CARD_DETAILS;
		CreditCardEvent creditCardEvent = new CreditCardEvent(correlationId, activityDate, activityId);
		try {
			String accountId = request.getAccountId();
			if (!Strings.isNullOrEmpty(accountId)) {
				logger.info("calling FetchCardDetails start Time : {} ", System.currentTimeMillis());
				ResponseEntity<FetchCardResponse> getCardRes = creditCardClient.getCreditCardDetails(correlationId,
						accountId);
				FetchCardResponse fetchCardResponse = getCardRes.getBody();
				if (fetchCardResponse != null && fetchCardResponse.getStatus().getStatusCode() == 0) {
					String productId = getCardRes.getBody().getCreditCard().getProductId();
					ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> response = commonServiceClient
							.getProductConfig(correlationId);
					List<ProductConfig> productConfigList = response.getBody().getData();
					ProductConfig productConfig = productConfigList.stream()
							.filter(e -> productId.equals(e.getProductCode())).findAny().orElse(null);
					if (productConfig != null) {
						ProductCodeData productCodeData = new ProductCodeData();
						productCodeData.setProductNameTH(productConfig.getProductNameTH());
						productCodeData.setProductNameEN(productConfig.getProductNameEN());
						productCodeData.setIconId(productConfig.getIconId());
						fetchCardResponse.setProductCodeData(productCodeData);
					}

					EStatementDetail eStatementDetail = getEStatementDetail(fetchCardResponse, crmId, correlationId);
					fetchCardResponse.setEStatementDetail(eStatementDetail);
										
					creditCardEvent = creditCardLogService.loadCardDetailsEvent(creditCardEvent,
							requestHeadersParameter, fetchCardResponse);
					creditCardLogService.logActivity(creditCardEvent);
					logger.info("calling FetchCardDetails end Time : {} ", System.currentTimeMillis());

					oneServiceResponse
							.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
									ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
					oneServiceResponse.setData(fetchCardResponse);
					return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
				} else {
					oneServiceResponse.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
							ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));

					return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
				}

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

	private EStatementDetail getEStatementDetail(FetchCardResponse fetchCardResponse, String crmId,
			String correlationId) {
		EStatementDetail result = new EStatementDetail();
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> responseWorkingProfileInfo = customerServiceClient
				.getCustomerProfile(crmId);
		CustGeneralProfileResponse profileResponse = responseWorkingProfileInfo.getBody().getData();
		if (profileResponse != null) {
			result.setEmailAddress(profileResponse.getEmailAddress());
			result.setEmailVerifyFlag(profileResponse.getEmailVerifyFlag());
			fetchCardResponse.getCreditCard().getCardEmail().setEmailAddress(profileResponse.getEmailAddress());
		}
		ApplyEStatementResponse applyEStatementResponse = applyEStatementService.getEStatement(crmId, correlationId);
		if (applyEStatementResponse != null) {
			processMappingEStatementFlag(fetchCardResponse, applyEStatementResponse);
		}
		return result;
	}
	
	private void processMappingEStatementFlag(FetchCardResponse fetchCardResponse,
			ApplyEStatementResponse applyEStatementResponse) {
		switch (fetchCardResponse.getCreditCard().getProductId()) {
		case "VABSIN":
		case "VBKDSI":
		case "VABSSN":
		case "VSOFAS":
		case "VTOPBR":
		case "VTTBCP":
		case "VSOSMT":
		case "VSOCHI":
		case "MSCHIL":
			fetchCardResponse.getCreditCard().getCardEmail().setEmaileStatementFlag(
					applyEStatementResponse.getCustomer().getStatementFlag().getECreditcardStatementFlag());
			break;
		case "VFPSTD":
			fetchCardResponse.getCreditCard().getCardEmail().setEmaileStatementFlag(
					applyEStatementResponse.getCustomer().getStatementFlag().getEReadyCashStatementFlag());
			break;
		case "C2G":
			fetchCardResponse.getCreditCard().getCardEmail().setEmaileStatementFlag(
					applyEStatementResponse.getCustomer().getStatementFlag().getECashToGoStatementFlag());
			break;
		default:
			break;
		}
	}

}
