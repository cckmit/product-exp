package com.tmb.oneapp.productsexpservice.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.address.ProvinceInfo;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustAddressProfileInfo;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustIndividualProfileInfo;
import com.tmb.oneapp.productsexpservice.model.request.AddressCommonSearchReq;

/**
 * Customer Profile Service
 * 
 * @author Witsanu
 *
 */
@Service
public class CustomerProfileService {

	private static final TMBLogger<CustomerProfileService> logger = new TMBLogger<>(CustomerProfileService.class);

	private CustomerServiceClient customerServiceClient;

	private CommonServiceClient commonServiceClient;

	public CustomerProfileService(CommonServiceClient commonServiceClient,
			CustomerServiceClient customerServiceClient) {
		this.customerServiceClient = customerServiceClient;
		this.commonServiceClient = commonServiceClient;
	}

	public CustIndividualProfileInfo getIndividualProfile(String crmId) {
		CustIndividualProfileInfo profileInfo = new CustIndividualProfileInfo();
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

			String addressInfo = generateAddressInfo(individualProfile);

			individualProfile.setAdddressInfo(addressInfo);
			CustAddressProfileInfo custAddressProfile = fillUpParamAddressInfo(provinceInfos, generalProfile);
			individualProfile.setAddress(custAddressProfile);
			individualProfile.setBirthdate(generalProfile.getIdBirthDate());
			individualProfile.setCitizenId(generalProfile.getCitizenId());
			individualProfile.setCustomerFullEN(generalProfile.getEngFname() + " " + generalProfile.getEngLname());
			individualProfile.setCustomerFullTh(generalProfile.getThaFname() + " " + generalProfile.getThaFname());
			individualProfile.setExpireDate(generalProfile.getIdExpireDate());
			individualProfile.setMobileNo(generalProfile.getPhoneNoFull());
			individualProfile.setNationality(generalProfile.getNationality());

//			individualProfile.setRemark(crmId);

		}
		return profileInfo;
	}

	private CustAddressProfileInfo fillUpParamAddressInfo(List<ProvinceInfo> provinceInfos,
			CustGeneralProfileResponse generalProfile) {
		CustAddressProfileInfo profileInfo = new CustAddressProfileInfo();
		return profileInfo;
	}

	private String generateAddressInfo(CustIndividualProfileInfo individualProfile) {
		// TODO Auto-generated method stub
		return null;
	}

}
