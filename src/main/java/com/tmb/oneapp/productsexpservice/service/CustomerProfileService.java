package com.tmb.oneapp.productsexpservice.service;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.address.District;
import com.tmb.common.model.address.Province;
import com.tmb.common.model.address.SubDistrict;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
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
		CustIndividualProfileInfo individualProfile = new CustIndividualProfileInfo();
		TmbOneServiceResponse<CustGeneralProfileResponse> custGeneralProfileRes = customerServiceClient
				.getCustomerProfile(crmId).getBody();

		if (!ResponseCode.SUCESS.getCode().equals(custGeneralProfileRes.getStatus().getCode())) {
			return null;
		}

		CustGeneralProfileResponse generalProfile = custGeneralProfileRes.getData();
		if (Objects.nonNull(generalProfile)) {
			AddressCommonSearchReq reqSearch = new AddressCommonSearchReq();
			reqSearch.setField("postcode");
			reqSearch.setSearch(generalProfile.getZipcode());
			ResponseEntity<TmbOneServiceResponse<List<Province>>> addressInfoRes = commonServiceClient
					.searchAddressByField(reqSearch);
			List<Province> provinceInfos = addressInfoRes.getBody().getData();

			CustAddressProfileInfo custAddressProfile = fillUpParamAddressInfo(provinceInfos, generalProfile);
			individualProfile.setAddress(custAddressProfile);
			individualProfile.setAdddressInfo(formatedAddressInline(custAddressProfile));
			individualProfile.setBirthdate(generalProfile.getIdBirthDate());
			individualProfile.setCitizenId(generalProfile.getCitizenId());
			individualProfile.setCustomerFullEN(generalProfile.getEngFname() + " " + generalProfile.getEngLname());
			individualProfile.setCustomerFullTh(generalProfile.getThaFname() + " " + generalProfile.getThaFname());
			individualProfile.setExpireDate(generalProfile.getIdExpireDate());
			individualProfile.setMobileNo(generalProfile.getPhoneNoFull());
			individualProfile.setNationality(generalProfile.getNationality());

		}
		return individualProfile;
	}

	private String formatedAddressInline(CustAddressProfileInfo custAddressProfile) {
		StringBuilder streetInLine = new StringBuilder();
		if (StringUtils.isNotBlank(custAddressProfile.getHouseNo())) {
			streetInLine.append(custAddressProfile.getHouseNo() + StringUtils.SPACE);
		}
		if (StringUtils.isNotBlank(custAddressProfile.getRoomNo())) {
			streetInLine.append(custAddressProfile.getRoomNo() + StringUtils.SPACE);
		}
		if (StringUtils.isNotBlank(custAddressProfile.getFloorNo())) {
			streetInLine.append(custAddressProfile.getFloorNo() + StringUtils.SPACE);
		}
		if (StringUtils.isNotBlank(custAddressProfile.getVillageOrbuilding())) {
			streetInLine.append(custAddressProfile.getVillageOrbuilding() + StringUtils.SPACE);
		}
		if (StringUtils.isNotBlank(custAddressProfile.getSoi())) {
			streetInLine.append(custAddressProfile.getSoi() + StringUtils.SPACE);
		}
		if (StringUtils.isNotBlank(custAddressProfile.getStreet())) {
			streetInLine.append(custAddressProfile.getStreet() + StringUtils.SPACE);
		}
		if (StringUtils.isNotBlank(custAddressProfile.getMoo())) {
			streetInLine.append(custAddressProfile.getMoo() + StringUtils.SPACE);
		}
		if (StringUtils.isNotBlank(custAddressProfile.getSubDistrictNameTh())) {
			streetInLine.append(custAddressProfile.getSubDistrictNameTh() + StringUtils.SPACE);
		}
		if (StringUtils.isNotBlank(custAddressProfile.getDistrictNameTh())) {
			streetInLine.append(custAddressProfile.getDistrictNameTh() + StringUtils.SPACE);
		}
		if (StringUtils.isNotBlank(custAddressProfile.getProvinceNameTh())) {
			streetInLine.append(custAddressProfile.getProvinceNameTh() + StringUtils.SPACE);
		}
		streetInLine.append(custAddressProfile.getZipcode());

		return streetInLine.toString();
	}

	private CustAddressProfileInfo fillUpParamAddressInfo(List<Province> provinceInfos,
			CustGeneralProfileResponse generalProfile) {
		CustAddressProfileInfo profileInfo = new CustAddressProfileInfo();

		profileInfo.setFloorNo(generalProfile.getFloorNo());
		profileInfo.setHouseNo(generalProfile.getHouseNo());
		profileInfo.setMoo(generalProfile.getMoo());
		profileInfo.setPostcode(generalProfile.getZipcode());
		profileInfo.setRoomNo(generalProfile.getRoomNo());
		profileInfo.setSoi(generalProfile.getSoi());
		profileInfo.setStreet(generalProfile.getStreet());
		profileInfo.setVillageOrbuilding(generalProfile.getVillageOrbuilding());
		profileInfo.setZipcode(generalProfile.getZipcode());
		profileInfo.setProvinceNameTh(generalProfile.getProvinceNameTh());
		profileInfo.setDistrictNameTh(generalProfile.getDistrictNameTh());
		profileInfo.setSubDistrictNameTh(generalProfile.getSubDistrictNameTh());

		District districtInfo = null;
		SubDistrict subDistrictInfo = null;
		Province provinceInfo = null;
		if (CollectionUtils.isNotEmpty(provinceInfos)) {
			provinceInfo = provinceInfos.get(0);
			profileInfo.setProvinceCode(provinceInfo.getProvinceCode());
			profileInfo.setProvinceNameEn(provinceInfo.getProvinceNameEn());
			profileInfo.setProvinceNameTh(provinceInfo.getProvinceNameTh());

		}

		if (Objects.nonNull(provinceInfo)) {
			for (District district : provinceInfo.getDistrictList()) {
				if (StringUtils.isNotEmpty(generalProfile.getSubDistrictNameTh())
						&& generalProfile.getSubDistrictNameTh().equals(district.getDistrictNameTh())) {
					districtInfo = district;
					profileInfo.setDistrictNameEn(districtInfo.getDistrictNameEn());
					profileInfo.setDistrictNameTh(districtInfo.getDistrictNameTh());
				}
			}
		}

		if (Objects.nonNull(districtInfo)) {
			for (SubDistrict subDistrict : districtInfo.getSubDistrictList()) {
				if (StringUtils.isNotEmpty(generalProfile.getSubDistrictNameTh())
						&& generalProfile.getSubDistrictNameTh().equals(subDistrict.getSubDistrictNameTh())) {
					subDistrictInfo = subDistrict;
					profileInfo.setSubDistrictNameEn(subDistrictInfo.getSubDistrictNameEn());
					profileInfo.setSubDistrictNameTh(subDistrictInfo.getSubDistrictNameTh());
				}
			}
		}

		return profileInfo;
	}

}
