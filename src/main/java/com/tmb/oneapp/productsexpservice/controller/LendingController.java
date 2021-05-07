package com.tmb.oneapp.productsexpservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustIndividualProfileInfo;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "Lending service")
public class LendingController {

	private static final TMBLogger<LendingController> logger = new TMBLogger<>(LendingController.class);

	private CustomerServiceClient customerServiceClient;

	@Autowired
	public LendingController(CustomerServiceClient customerServiceClient) {
		this.customerServiceClient = customerServiceClient;
	}

	@LogAround
	@PostMapping(value = "/loan/get-account-detail", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TmbOneServiceResponse<CustIndividualProfileInfo>> getIndividualProfileInfo(
			@RequestHeader Map<String, String> requestHeadersParameter) {
		String crmId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CRMID);
		customerServiceClient.getCustomerProfile(crmId);
		return null;
	}

}
