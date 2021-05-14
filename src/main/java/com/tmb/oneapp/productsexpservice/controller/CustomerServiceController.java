package com.tmb.oneapp.productsexpservice.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustIndividualProfileInfo;
import com.tmb.oneapp.productsexpservice.model.request.AddressCommonSearchReq;
import com.tmb.oneapp.productsexpservice.service.CustomerProfileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "Lend Customer information service")
public class CustomerServiceController {

	private static final TMBLogger<CustomerServiceController> logger = new TMBLogger<>(CustomerServiceController.class);

	private CustomerProfileService customerProfileService;
	private CommonServiceClient commonServiceClient;

	@Autowired
	public CustomerServiceController(CustomerProfileService customerProfileService,
			CommonServiceClient commonServiceClient) {
		this.customerProfileService = customerProfileService;
		this.commonServiceClient = commonServiceClient;
	}

	@LogAround
	@PostMapping(value = "/fetch-customer-info", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get customer info details")
	public ResponseEntity<TmbOneServiceResponse<CustIndividualProfileInfo>> getIndividualProfileInfo(
			@RequestHeader Map<String, String> requestHeadersParameter) {
		String crmId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CRMID);
		TmbOneServiceResponse<CustIndividualProfileInfo> customerIndividualProfileInfo = new TmbOneServiceResponse<>();
		CustIndividualProfileInfo individualProfileInfo = customerProfileService.getIndividualProfile(crmId);
		if (Objects.isNull(individualProfileInfo)) {
			customerIndividualProfileInfo.setData(null);// empty
			customerIndividualProfileInfo.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(),
					ResponseCode.SUCESS.getMessage(), ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		} else {
			customerIndividualProfileInfo.setData(individualProfileInfo);
			customerIndividualProfileInfo.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(),
					ResponseCode.SUCESS.getMessage(), ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		}

		return ResponseEntity.ok().body(customerIndividualProfileInfo);
	}

	@LogAround
	@GetMapping(value = "/zipcode")
	@ApiOperation(value = "Get Address info details by post code")
	public ResponseEntity<TmbOneServiceResponse<List<Province>>> getMasterAddessInfo(
			@RequestParam(value = "code") String postCode) {
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

}
