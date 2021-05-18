package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.response.CodeEntry;
import com.tmb.oneapp.productsexpservice.model.response.lending.WorkProfileInfoResponse;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.LendingRslStatusResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

/**
 * LendingServiceClient to retrieve lending data
 */
@FeignClient(name = "${lending.service.name}", url = "${lending.service.url}")
public interface LendingServiceClient {

	/**
	 * Call RSL System to get application status
	 *
	 * @return RSL application statuses
	 */
	@GetMapping(value = "/apis/lending-service/rsl/status")
	ResponseEntity<TmbOneServiceResponse<List<LendingRslStatusResponse>>> getLendingRslStatus(
			@RequestHeader(X_CORRELATION_ID) String correlationId, @RequestHeader(HEADER_CITIZEN_ID) String citizenId,
			@RequestHeader(HEADER_MOBILE_NO) String mobileNo);

	/**
	 * Call RSL Criteria for WorkStatusInfo
	 * 
	 * @param correlationId
	 * @return
	 */
	@GetMapping(value = "/apis/lending-service/criteria/status")
	ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getWorkStatusInfo(
			@RequestHeader(X_CORRELATION_ID) String correlationId);

	/**
	 * Call RSL Criteria for OccupationByOccupationCode
	 * 
	 * @param correlationId
	 * @param reference
	 * @return
	 */
	@GetMapping(value = "/apis/lending-service/criteria/status/{entrycode}")
	ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getWorkStatusInfo(
			@RequestHeader(X_CORRELATION_ID) String correlationId, @PathVariable("entrycode") String reference);

	/**
	 * Call RSL Criteria for get business type information
	 * 
	 * @param correlationId
	 * @return
	 */
	@GetMapping(value = "/apis/lending-service/criteria/businesstype")
	ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getBusinessTypeInfo(
			@RequestHeader(X_CORRELATION_ID) String correlationId);

	/**
	 * Call RSL Criteria for get source of income
	 * 
	 * @param correlationId
	 * @return
	 */
	@GetMapping(value = "/apis/lending-service/criteria/income/{entryCode}")
	ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getSourceOfIncomeInfo(
			@RequestHeader(X_CORRELATION_ID) String correlationId, @PathVariable("entryCode") String reference);

	/**
	 * Call RSL Criteria for country information
	 * 
	 * @param correlationId
	 * @return
	 */
	@GetMapping(value = "/apis/lending-service/criteria/country")
	ResponseEntity<TmbOneServiceResponse<List<CodeEntry>>> getCountryList(
			@RequestHeader(X_CORRELATION_ID) String correlationId);

	@GetMapping(value = "/apis/lending-service/fetch-working-info")
	ResponseEntity<TmbOneServiceResponse<WorkProfileInfoResponse>> getWorkInformationWithProfile(
			@RequestHeader(X_CORRELATION_ID) String correlationId,
			@RequestParam(value = "occupationcode") String occupationCode,
			@RequestParam(value = "businesstypecode") String businessTypeCode,
			@RequestParam(value = "countryofincome") String countryOfIncome);

}
