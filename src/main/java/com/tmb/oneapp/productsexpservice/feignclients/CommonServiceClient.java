package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.*;
import com.tmb.common.model.address.Province;
import com.tmb.common.model.loan.stagingbar.LoanStagingbar;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.common.findbyfundhouse.FundHouseResponse;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.lending.loan.LoanStagingbarRequest;
import com.tmb.oneapp.productsexpservice.model.request.AddressCommonSearchReq;
import com.tmb.oneapp.productsexpservice.model.response.NodeDetails;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "${feign.common.service.name}", url = "${feign.common.service.url}")
public interface CommonServiceClient {

	@GetMapping(value = "/apis/common/fetch/product-config")
	ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> getProductConfig(
			@RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationID);

	@GetMapping(value = "/apis/common/internal/common/config")
	ResponseEntity<TmbOneServiceResponse<List<CommonData>>> getCommonConfigByModule(
			@RequestHeader("X-Correlation-ID") String correlationId, @RequestParam("search") String search);

	@GetMapping(value = "/apis/common/product/application/roadmap")
	ResponseEntity<TmbOneServiceResponse<List<NodeDetails>>> getProductApplicationRoadMap();

	@GetMapping(value = "/apis/common/internal/common/config")
	@ApiOperation("Get Common Config by Module")
	ResponseEntity<TmbOneServiceResponse<List<CommonData>>> getCommonConfig(
			@RequestHeader("X-Correlation-ID") String correlationId, @RequestParam("search") String search);

	@PostMapping(value = "/apis/common/internal/address")
	ResponseEntity<TmbOneServiceResponse<List<Province>>> searchAddressByField(
			@RequestBody(required = false) AddressCommonSearchReq reqSearch);

	@GetMapping(value = "/apis/common/internal/lovmaster/config")
	ResponseEntity<TmbOneServiceResponse<LovMaster>> getLookupMasterModule(@RequestParam("code") String code);

	@GetMapping(value = "/apis/common/get-interest-rate")
	ResponseEntity<TmbOneServiceResponse<List<LoanOnlineInterestRate>>> getInterestRateAll();

	@GetMapping(value = "/apis/common/get-range-income")
	ResponseEntity<TmbOneServiceResponse<List<LoanOnlineRangeIncome>>> getRangeIncomeAll();

	/**
	 * Get term and condition by channel and service code by calling common api
	 *
	 * @param correlationId the
	 * @param serviceCode   the service code
	 * @param channel       the channel
	 * @return the term and condition response
	 */
	@GetMapping(value = "/apis/common/internal/term-condition/service/{serviceCode}/{channel}")
	ResponseEntity<TmbOneServiceResponse<TermAndConditionResponseBody>> getTermAndConditionByServiceCodeAndChannel(
			@RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
			@PathVariable("serviceCode") String serviceCode, @PathVariable("channel") String channel);

	@PostMapping(value = "/apis/common/fetch/staging-bar")
	TmbOneServiceResponse<LoanStagingbar> fetchLoanStagingBar(
			@RequestBody(required = true) LoanStagingbarRequest request);

	@GetMapping(value = "${common.service.fund.house.url}")
	TmbOneServiceResponse<FundHouseResponse> fetchBankInfoByFundHouse(@RequestHeader Map<String, String> headers,
																	  @RequestParam("code") String fundHouseCode);



}
