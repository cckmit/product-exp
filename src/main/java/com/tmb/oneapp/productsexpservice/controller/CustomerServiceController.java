package com.tmb.oneapp.productsexpservice.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustIndividualProfileInfo;
import com.tmb.oneapp.productsexpservice.service.CustomerProfileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "Lend Customer information service")
public class CustomerServiceController {

	private static final TMBLogger<CustomerServiceController> logger = new TMBLogger<>(CustomerServiceController.class);

	private CustomerProfileService customerProfileService;

	@Autowired
	public CustomerServiceController(CustomerProfileService customerProfileService) {
		this.customerProfileService = customerProfileService;
	}

	@LogAround
	@PostMapping(value = "/customerservice/get", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get customer info details")
	public ResponseEntity<TmbOneServiceResponse<CustIndividualProfileInfo>> getIndividualProfileInfo(
			@RequestHeader Map<String, String> requestHeadersParameter) {
		String crmId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CRMID);
		TmbOneServiceResponse<CustIndividualProfileInfo> customerIndividualProfileInfo = new TmbOneServiceResponse<>();
		CustIndividualProfileInfo individualProfileInfo = customerProfileService.getIndividualProfile(crmId);
		customerIndividualProfileInfo.setData(individualProfileInfo);
		customerIndividualProfileInfo.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                    ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
				return ResponseEntity.ok().body(customerIndividualProfileInfo);
	}

}
