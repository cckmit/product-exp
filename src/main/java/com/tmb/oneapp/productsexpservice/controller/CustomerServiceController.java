package com.tmb.oneapp.productsexpservice.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.address.Province;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustIndividualProfileInfo;
import com.tmb.oneapp.productsexpservice.model.request.AddressCommonSearchReq;
import com.tmb.oneapp.productsexpservice.model.request.WorkingInfoReq;
import com.tmb.oneapp.productsexpservice.model.response.CodeEntry;
import com.tmb.oneapp.productsexpservice.model.response.WorkingInfoResponse;
import com.tmb.oneapp.productsexpservice.service.CustomerProfileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(tags = "Lend Customer information service")
public class CustomerServiceController {

	private static final TMBLogger<CustomerServiceController> logger = new TMBLogger<>(CustomerServiceController.class);

	private CustomerProfileService customerProfileService;
	private CommonServiceClient commonServiceClient;
	private LendingServiceClient lendingServiceClient;

	@Autowired
	public CustomerServiceController(CustomerProfileService customerProfileService,
			CommonServiceClient commonServiceClient, LendingServiceClient lendingServiceClient) {
		this.customerProfileService = customerProfileService;
		this.commonServiceClient = commonServiceClient;
		this.lendingServiceClient = lendingServiceClient;
	}

	/**
	 * Service for get individual information
	 * 
	 * @param requestHeadersParameter
	 * @return
	 */
	@LogAround
	@PostMapping(value = "/fetch-customer-info", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get customer info details")
	public ResponseEntity<TmbOneServiceResponse<CustIndividualProfileInfo>> getIndividualProfileInfo(
			@RequestHeader Map<String, String> headers) {
		String crmId = headers.get(ProductsExpServiceConstant.X_CRMID);
		TmbOneServiceResponse<CustIndividualProfileInfo> customerIndividualProfileInfo = new TmbOneServiceResponse<>();
		CustIndividualProfileInfo individualProfileInfo = customerProfileService.getIndividualProfile(crmId);
		if (Objects.isNull(individualProfileInfo)) {
			customerIndividualProfileInfo.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(),
					ResponseCode.FAILED.getMessage(), ResponseCode.FAILED.getService(), ResponseCode.FAILED.getDesc()));
		} else {
			customerIndividualProfileInfo.setData(individualProfileInfo);
			customerIndividualProfileInfo.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(),
					ResponseCode.SUCESS.getMessage(), ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		}

		return ResponseEntity.ok().body(customerIndividualProfileInfo);
	}

	/**
	 * Service for get zipcode
	 * 
	 * @param postCode
	 * @return
	 */
	@LogAround
	@GetMapping(value = "/zipcode")
	@ApiOperation(value = "Get Address info details by post code")
	public ResponseEntity<TmbOneServiceResponse<List<Province>>> getZipcodeInfo(
			@RequestParam(value = "code") String postCode, @RequestHeader Map<String, String> header) {
		if (StringUtils.isEmpty(postCode)) {
			return ResponseEntity.ok().build();
		}
		AddressCommonSearchReq searchReq = new AddressCommonSearchReq();
		searchReq.setField("postcode");
		searchReq.setSearch(postCode);
		ResponseEntity<TmbOneServiceResponse<List<Province>>> provinces = commonServiceClient
				.searchAddressByField(searchReq);
		TmbOneServiceResponse<List<Province>> response = new TmbOneServiceResponse();
		if (Objects.nonNull(provinces.getBody()) && CollectionUtils.isNotEmpty(provinces.getBody().getData())) {
			response.setData(provinces.getBody().getData());
			response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		} else {
			response.setData(null);
			response.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), ResponseCode.FAILED.getDesc()));
		}

		return ResponseEntity.ok().body(response);
	}

	/**
	 * Service for get working information
	 * 
	 * @param workingReq
	 * @param requestHeaders
	 * @return
	 */
	@LogAround
	@PostMapping(value = "/fetch-working-info", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get customer working information details")
	public ResponseEntity<TmbOneServiceResponse<WorkingInfoResponse>> getWorkingInformation(WorkingInfoReq workingReq,
			@RequestHeader Map<String, String> headers) {
		String correlationId = headers.get(ProductsExpServiceConstant.X_CORRELATION_ID);
		String crmId = headers.get(ProductsExpServiceConstant.X_CRMID);
		TmbOneServiceResponse<WorkingInfoResponse> response = new TmbOneServiceResponse();
		try {
			WorkingInfoResponse workInformation = customerProfileService.getWorkingInformation(crmId, correlationId);
			response.setData(workInformation);
			response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		} catch (Exception e) {
			response.setData(null);
			response.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), ResponseCode.FAILED.getDesc()));
		}

		return ResponseEntity.ok().body(response);
	}

	/**
	 * Service for dependency criteria for working status
	 * 
	 * @param occupationEntryCode
	 * @param bustypeEntryCode
	 * @param requestHeaders
	 * @return
	 */
	@LogAround
	@GetMapping(value = "/fetch-working-status")
	@ApiOperation(value = "Get dependency working information details links")
	public ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getWorkingStatusDependency(
			@ApiParam(value = "x-correlation-id", defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da") @RequestHeader Map<String, String> headers) {
		String correlationId = headers.get(ProductsExpServiceConstant.X_CORRELATION_ID);
		TmbOneServiceResponse<List<CodeEntry>> response = new TmbOneServiceResponse();
		try {
			ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> lendingResponse = lendingServiceClient
					.getWorkStatusInfo(correlationId);
			if (Objects.nonNull(lendingResponse.getBody())) {
				response.setData(lendingResponse.getBody().getData());
			}
			response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		} catch (Exception e) {
			response.setData(null);
			response.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), ResponseCode.FAILED.getDesc()));
		}

		return ResponseEntity.ok().body(response);
	}

	/**
	 * Service for dependency criteria for working dependency by occupation code
	 * 
	 * @param occupationEntryCode
	 * @param bustypeEntryCode
	 * @param requestHeaders
	 * @return
	 */
	@LogAround
	@GetMapping(value = "/fetch-working-status/{occupationEntryCode}")
	@ApiOperation(value = "Get dependency working information details links")
	public ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getWorkingDependencyByOccupationCode(
			@PathVariable String occupationEntryCode, @RequestHeader Map<String, String> headers) {
		String correlationId = headers.get(ProductsExpServiceConstant.X_CORRELATION_ID);
		TmbOneServiceResponse<List<CodeEntry>> response = new TmbOneServiceResponse();
		try {
			ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> lendingResponse = lendingServiceClient
					.getWorkStatusInfo(correlationId, occupationEntryCode);
			if (Objects.nonNull(lendingResponse.getBody())) {
				response.setData(lendingResponse.getBody().getData());
			}
			response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		} catch (Exception e) {
			response.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), ResponseCode.FAILED.getDesc()));
		}

		return ResponseEntity.ok().body(response);
	}

	/**
	 * Service for dependency criteria for businesstype info
	 * 
	 * @param occupationEntryCode
	 * @param bustypeEntryCode
	 * @param requestHeaders
	 * @return
	 */
	@LogAround
	@GetMapping(value = "/fetch-business-type")
	@ApiOperation(value = "Get dependency working information details links")
	public ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getWorkingDependencyBusinessType(
			@RequestHeader Map<String, String> headers) {
		String correlationId = headers.get(ProductsExpServiceConstant.X_CORRELATION_ID);
		TmbOneServiceResponse<List<CodeEntry>> response = new TmbOneServiceResponse();
		try {
			ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> lendingResponse = lendingServiceClient
					.getBusinessTypeInfo(correlationId);
			if (Objects.nonNull(lendingResponse.getBody())) {
				response.setData(lendingResponse.getBody().getData());
			}
			response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		} catch (Exception e) {
			response.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), ResponseCode.FAILED.getDesc()));
		}

		return ResponseEntity.ok().body(response);
	}

	/**
	 * Service for dependency criteria for businesstype info
	 * 
	 * @param occupationEntryCode
	 * @param bustypeEntryCode
	 * @param requestHeaders
	 * @return
	 */
	@LogAround
	@GetMapping(value = "/fetch-business-type/{entrycode}")
	@ApiOperation(value = "Get dependency working information details links")
	public ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getWorkingDependencyBusinessType(
			@PathVariable String entrycode, @RequestHeader Map<String, String> headers) {
		String correlationId = headers.get(ProductsExpServiceConstant.X_CORRELATION_ID);
		TmbOneServiceResponse<List<CodeEntry>> response = new TmbOneServiceResponse();
		try {
			ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> lendingResponse = lendingServiceClient
					.getBusinessSubTypeInfo(correlationId);
			if (Objects.nonNull(lendingResponse.getBody())) {
				response.setData(lendingResponse.getBody().getData());
			}
			response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		} catch (Exception e) {
			response.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), ResponseCode.FAILED.getDesc()));
		}

		return ResponseEntity.ok().body(response);
	}

	/**
	 * Service for dependency criteria for income information
	 * 
	 * @param occupationEntryCode
	 * @param incomeEntryCode
	 * @param requestHeaders
	 * @return
	 */
	@LogAround
	@GetMapping(value = "/fetch-working-income/{entrycode}")
	@ApiOperation(value = "Get dependency income information details links")
	public ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getCountryIncomeSourceDependency(
			@PathVariable String entrycode, @RequestHeader Map<String, String> headers) {
		String correlationId = headers.get(ProductsExpServiceConstant.X_CORRELATION_ID);
		TmbOneServiceResponse<List<CodeEntry>> response = new TmbOneServiceResponse();
		try {
			ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> lendingResponse = lendingServiceClient
					.getSourceOfIncomeInfo(correlationId, entrycode);
			if (Objects.nonNull(lendingResponse.getBody())) {
				response.setData(lendingResponse.getBody().getData());
			}
			response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		} catch (Exception e) {
			response.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), ResponseCode.FAILED.getDesc()));
		}

		return ResponseEntity.ok().body(response);
	}

	/**
	 * Service for dependency criteria for income information
	 * 
	 * @param occupationEntryCode
	 * @param incomeEntryCode
	 * @param requestHeaders
	 * @return
	 */
	@LogAround
	@GetMapping(value = "/fetch-country")
	@ApiOperation(value = "Get dependency income information details links")
	public ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getCountryDependency(
			@RequestHeader Map<String, String> headers) {
		String correlationId = headers.get(ProductsExpServiceConstant.X_CORRELATION_ID);
		TmbOneServiceResponse<List<CodeEntry>> response = new TmbOneServiceResponse();
		try {
			ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> lendingResponse = lendingServiceClient
					.getCountryList(correlationId);
			if (Objects.nonNull(lendingResponse.getBody())) {
				response.setData(lendingResponse.getBody().getData());
			}
			response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		} catch (Exception e) {
			response.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), ResponseCode.FAILED.getDesc()));
		}

		return ResponseEntity.ok().body(response);
	}

}
