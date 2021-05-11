package com.tmb.oneapp.productsexpservice.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.address.ProvinceInfo;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustIndividualProfileInfo;
import com.tmb.oneapp.productsexpservice.model.request.AddressCommonSearchReq;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "Lend Customer information service")
public class CustomerServiceController {

	private static final TMBLogger<CustomerServiceController> logger = new TMBLogger<>(CustomerServiceController.class);

	private CustomerServiceClient customerServiceClient;

	private CommonServiceClient commonServiceClient;

	@Autowired
	public CustomerServiceController(CustomerServiceClient customerServiceClient,
			CommonServiceClient commonServiceClient) {
		this.customerServiceClient = customerServiceClient;
		this.commonServiceClient = commonServiceClient;
	}

	@LogAround
	@PostMapping(value = "/customerservice/get", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TmbOneServiceResponse<CustIndividualProfileInfo>> getIndividualProfileInfo(
			@RequestHeader Map<String, String> requestHeadersParameter) {
		String crmId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CRMID);
		TmbOneServiceResponse<CustIndividualProfileInfo> customerIndividualProfileInfo = new TmbOneServiceResponse<>();
		TmbOneServiceResponse<CustGeneralProfileResponse> custGeneralProfileRes = customerServiceClient
				.getCustomerProfile(crmId).getBody();
		CustGeneralProfileResponse generalProfile = custGeneralProfileRes.getData();
		if (Objects.nonNull(generalProfile)) {
			CustIndividualProfileInfo individualProfile = new CustIndividualProfileInfo();
			AddressCommonSearchReq reqSearch = new AddressCommonSearchReq();
			reqSearch.setField("postcode");
			reqSearch.setSearch(generalProfile.getZipcode());
			ResponseEntity<TmbOneServiceResponse<List<ProvinceInfo>>> addressInfoRes = commonServiceClient
					.searchAddressByField(reqSearch);
			List<ProvinceInfo> provinceInfos = addressInfoRes.getBody().getData();
			
			customerIndividualProfileInfo.setData(individualProfile);
		}

		return ResponseEntity.ok().body(customerIndividualProfileInfo);
	}

}
