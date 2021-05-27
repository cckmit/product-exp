package com.tmb.oneapp.productsexpservice.service;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.rpc.ServiceException;

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
import com.tmb.common.model.legacy.rsl.ob.individual.InstantIndividual;
import com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.response.ResponseInstantLoanGetCustInfo;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanInstantGetCustomerInfoClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustAddressProfileInfo;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustIndividualProfileInfo;
import com.tmb.oneapp.productsexpservice.model.request.AddressCommonSearchReq;
import com.tmb.oneapp.productsexpservice.model.response.CodeEntry;
import com.tmb.oneapp.productsexpservice.model.response.DependDefaultEntry;
import com.tmb.oneapp.productsexpservice.model.response.WorkingInfoResponse;
import com.tmb.oneapp.productsexpservice.model.response.lending.WorkProfileInfoResponse;

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

	private LendingServiceClient lendingServiceClient;

	private CommonServiceClient commonServiceClient;

	private LoanInstantGetCustomerInfoClient instanceCustomerInfoClient;

	public CustomerProfileService(CommonServiceClient commonServiceClient, CustomerServiceClient customerServiceClient,
			LendingServiceClient lendingServiceClient, LoanInstantGetCustomerInfoClient instanceCustomerInfoClient) {
		this.customerServiceClient = customerServiceClient;
		this.commonServiceClient = commonServiceClient;
		this.lendingServiceClient = lendingServiceClient;
		this.instanceCustomerInfoClient = instanceCustomerInfoClient;
	}

	/**
	 * Get individual profile information
	 * 
	 * @param crmId
	 * @return
	 */
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
			reqSearch.setSearch(generalProfile.getCurrentAddrZipcode());
			ResponseEntity<TmbOneServiceResponse<List<Province>>> addressInfoRes = commonServiceClient
					.searchAddressByField(reqSearch);
			List<Province> provinceInfos = addressInfoRes.getBody().getData();

			CustAddressProfileInfo custAddressProfile = fillUpParamCurrentAddressInfo(provinceInfos, generalProfile);
			individualProfile.setAddress(custAddressProfile);
			individualProfile.setAdddressInfo(formatedAddressInline(custAddressProfile));
			individualProfile.setBirthdate(generalProfile.getIdBirthDate());
			individualProfile.setCitizenId(generalProfile.getCitizenId());
			individualProfile.setCustomerFullEN(generalProfile.getEngFname() + " " + generalProfile.getEngLname());
			individualProfile.setCustomerFullTh(generalProfile.getThaFname() + " " + generalProfile.getThaLname());
			individualProfile.setExpireDate(generalProfile.getIdExpireDate());
			individualProfile.setMobileNo(generalProfile.getPhoneNoFull());
			individualProfile.setNationality(generalProfile.getNationality());
			individualProfile.setFirstNameTh(generalProfile.getThaFname());
			individualProfile.setLastNameTh(generalProfile.getThaLname());

			individualProfile.setIdType(generalProfile.getIdType());
			individualProfile.setIdNo(generalProfile.getIdNo());

		}
		return individualProfile;
	}

	/**
	 * Create formate address in line formate
	 * 
	 * @param custAddressProfile
	 * @return
	 */
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

	/**
	 * Fill param
	 * 
	 * @param provinceInfos
	 * @param generalProfile
	 * @return
	 */
	private CustAddressProfileInfo fillUpParamCurrentAddressInfo(List<Province> provinceInfos,
			CustGeneralProfileResponse generalProfile) {
		CustAddressProfileInfo profileInfo = new CustAddressProfileInfo();

		profileInfo.setFloorNo(generalProfile.getCurrentAddrFloorNo());
		profileInfo.setHouseNo(generalProfile.getCurrentAddrHouseNo());
		profileInfo.setMoo(generalProfile.getCurrentAddrMoo());
		profileInfo.setPostcode(generalProfile.getCurrentAddrZipcode());
		profileInfo.setRoomNo(generalProfile.getCurrentAddrRoomNo());
		profileInfo.setSoi(generalProfile.getCurrentAddrSoi());
		profileInfo.setStreet(generalProfile.getCurrentAddrStreet());
		profileInfo.setVillageOrbuilding(generalProfile.getCurrentAddrVillageOrbuilding());
		profileInfo.setZipcode(generalProfile.getCurrentAddrZipcode());

		profileInfo.setProvinceNameTh(generalProfile.getCurrentAddrProvinceNameTh());
		profileInfo.setProvinceCode(generalProfile.getCurrentAddrprovinceCode());

		profileInfo.setDistrictNameTh(generalProfile.getCurrentAddrdistrictNameTh());
		profileInfo.setSubDistrictNameTh(generalProfile.getCurrentAddrSubDistrictNameTh());

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

				if (StringUtils.isNotEmpty(generalProfile.getCurrentAddrdistrictNameTh())
						&& generalProfile.getCurrentAddrdistrictNameTh().equals(district.getDistrictNameTh())) {
					districtInfo = district;
					profileInfo.setDistrictNameEn(districtInfo.getDistrictNameEn());
					profileInfo.setDistrictNameTh(districtInfo.getDistrictNameTh());
				}
			}
		}

		if (Objects.nonNull(districtInfo)) {
			for (SubDistrict subDistrict : districtInfo.getSubDistrictList()) {

				if (StringUtils.isNotEmpty(generalProfile.getCurrentAddrSubDistrictNameTh()) && generalProfile
						.getCurrentAddrSubDistrictNameTh().equals(subDistrict.getSubDistrictNameTh())) {
					subDistrictInfo = subDistrict;
					profileInfo.setSubDistrictNameEn(subDistrictInfo.getSubDistrictNameEn());
					profileInfo.setSubDistrictNameTh(subDistrictInfo.getSubDistrictNameTh());
				}
			}
		}

		return profileInfo;
	}

	/**
	 * Look up working information
	 * 
	 * @param crmId
	 * @param businessTypeCode
	 * @param countryOfIncome
	 * @param occupationCode
	 * @return
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public WorkingInfoResponse getWorkingInformation(String crmId, String correlationId)
			throws RemoteException, ServiceException {
		WorkingInfoResponse response = new WorkingInfoResponse();
		ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> responseWorkingProfileInfo = customerServiceClient
				.getCustomerProfile(crmId);
		CustGeneralProfileResponse profileResponse = responseWorkingProfileInfo.getBody().getData();
		if (Objects.isNull(profileResponse)) {
			return response;
		}

		AddressCommonSearchReq reqSearch = new AddressCommonSearchReq();
		reqSearch.setField("postcode");
		reqSearch.setSearch(profileResponse.getWorkAddrZipcode());
		ResponseEntity<TmbOneServiceResponse<List<Province>>> addressInfoRes = commonServiceClient
				.searchAddressByField(reqSearch);
		List<Province> provinceInfos = addressInfoRes.getBody().getData();
		CustAddressProfileInfo workingAddress = fillUpParamWorkAddressInfo(provinceInfos, profileResponse);
		String requestBusinessType = profileResponse.getBusinessTypeCode().substring(0, 1);
		ResponseEntity<TmbOneServiceResponse<WorkProfileInfoResponse>> workingProfile = lendingServiceClient
				.getWorkInformationWithProfile(correlationId, profileResponse.getOccupationCode(), requestBusinessType,
						profileResponse.getNationality());

		WorkProfileInfoResponse workProfileResponse = workingProfile.getBody().getData();
		if (Objects.nonNull(workProfileResponse)) {
			response.setProfilesDependency(workProfileResponse);
			response.setEmploymentName(profileResponse.getWorkEmploymentName());
			response.setWorkingAddress(workingAddress);
			response.setWorkingPhoneNo(profileResponse.getWorkPhoneNo());
			response.setWorkingPhoneNoExt(profileResponse.getWorkPhoneNoExt());
		}

		String profile = crmId.substring(16, crmId.length());
		ResponseInstantLoanGetCustInfo responeInfo = instanceCustomerInfoClient.getInstantCustomerInfo(profile);
		InstantIndividual[] indi = responeInfo.getBody().getInstantIndividual();
		InstantIndividual instandIndividual = indi[0];
		response.setProfessionalCode(instandIndividual.getProfessionalCode());
		if ("01".equals(instandIndividual.getEmploymentStatus())) {
			response.setIncomeBaseSalary(instandIndividual.getIncomeBasicSalary().toString());
		} else {
			response.setIncomeBaseSalary(instandIndividual.getInTotalIncome().toString());
		}

		response.setIncomeDeclared(instandIndividual.getIncomeDeclared().toString());

		createdIncomeCriteriaDependency(response, response.getIncomeBaseSalary());

		return response;
	}

	/**
	 * Create income criteria dependency
	 * 
	 * @param response
	 * @param incomeBaseSalary
	 */
	private void createdIncomeCriteriaDependency(WorkingInfoResponse response, String incomeBaseSalary) {
		DependDefaultEntry defaultEntry = new DependDefaultEntry();
		List<CodeEntry> entry = new ArrayList<CodeEntry>();
		CodeEntry entryLevelOne = new CodeEntry();
		entryLevelOne.setEntryName("15000-29999");
		entryLevelOne.setEntryCode("30000");
		entry.add(entryLevelOne);
		CodeEntry entryLevelTwo = new CodeEntry();
		entryLevelTwo.setEntryName("30000-49999");
		entryLevelTwo.setEntryCode("50000");
		entry.add(entryLevelTwo);

		CodeEntry entryLevelThree = new CodeEntry();
		entryLevelThree.setEntryName("50000-99999");
		entryLevelThree.setEntryCode("100000");
		entry.add(entryLevelThree);

		CodeEntry entryLevelFour = new CodeEntry();
		entryLevelFour.setEntryName("100000-199999");
		entryLevelFour.setEntryCode("200000");
		entry.add(entryLevelFour);
		defaultEntry.setEntry(entry);

		CodeEntry match = selectMatchDependency(defaultEntry, incomeBaseSalary);
		defaultEntry.setName(match.getEntryName());
		defaultEntry.setValue(match.getEntryCode());

		defaultEntry.setEntry(entry);
		response.setIncomeDependency(defaultEntry);

	}

	/**
	 * Selection match dependency value
	 * 
	 * @param defaultEntry
	 * @param incomeBaseSalary
	 * @return
	 */
	private CodeEntry selectMatchDependency(DependDefaultEntry defaultEntry, String incomeBaseSalary) {
		List<CodeEntry> codeEntry = defaultEntry.getEntry();
		CodeEntry returnEntry = codeEntry.get(codeEntry.size()-1);
		if(Integer.parseInt(incomeBaseSalary)<=15000) {
			return codeEntry.get(0);
		}
		for (CodeEntry entry : codeEntry) {
			int maxAmount = Integer.parseInt(entry.getEntryCode());
			if(maxAmount>=Integer.parseInt(incomeBaseSalary)) {
				returnEntry = entry;
			}
		}
		return returnEntry;
	}

	/**
	 * Look up working address
	 * 
	 * @param provinceInfos
	 * @param profileResponse
	 * @return
	 */
	private CustAddressProfileInfo fillUpParamWorkAddressInfo(List<Province> provinceInfos,
			CustGeneralProfileResponse profileResponse) {
		CustAddressProfileInfo custAddressProfile = new CustAddressProfileInfo();
		custAddressProfile.setDistrictNameTh(profileResponse.getWorkAddrdistrictNameTh());
		custAddressProfile.setFloorNo(profileResponse.getWorkAddrFloorNo());
		custAddressProfile.setHouseNo(profileResponse.getWorkAddrHouseNo());
		custAddressProfile.setMoo(profileResponse.getWorkAddrMoo());
		custAddressProfile.setPostcode(profileResponse.getWorkAddrZipcode());
		custAddressProfile.setProvinceCode(profileResponse.getWorkAddrprovinceCode());
		custAddressProfile.setRoomNo(profileResponse.getWorkAddrRoomNo());
		custAddressProfile.setSoi(profileResponse.getWorkAddrSoi());
		custAddressProfile.setStreet(profileResponse.getWorkAddrStreet());
		custAddressProfile.setVillageOrbuilding(profileResponse.getWorkAddrVillageOrbuilding());
		custAddressProfile.setZipcode(profileResponse.getWorkAddrZipcode());

		District districtInfo = null;
		Province provinceInfo = null;

		if (CollectionUtils.isNotEmpty(provinceInfos)) {
			provinceInfo = provinceInfos.get(0);
			custAddressProfile.setProvinceCode(provinceInfo.getProvinceCode());
			custAddressProfile.setProvinceNameEn(provinceInfo.getProvinceNameEn());
			custAddressProfile.setProvinceNameTh(provinceInfo.getProvinceNameTh());
		}

		if (Objects.nonNull(provinceInfo)) {
			for (District district : provinceInfo.getDistrictList()) {

				if (StringUtils.isNotEmpty(profileResponse.getWorkAddrdistrictNameTh())
						&& profileResponse.getWorkAddrdistrictNameTh().equals(district.getDistrictNameTh())) {
					districtInfo = district;
					custAddressProfile.setDistrictNameEn(districtInfo.getDistrictNameEn());
					custAddressProfile.setDistrictNameTh(districtInfo.getDistrictNameTh());
				}
			}
		}

		if (Objects.nonNull(districtInfo)) {
			for (SubDistrict subDistrict : districtInfo.getSubDistrictList()) {
				if (StringUtils.isNotEmpty(profileResponse.getWorkAddrSubDistrictNameTh())
						&& profileResponse.getWorkAddrSubDistrictNameTh().equals(subDistrict.getSubDistrictNameTh())) {
					custAddressProfile.setSubDistrictNameEn(subDistrict.getSubDistrictNameEn());
					custAddressProfile.setSubDistrictNameTh(subDistrict.getSubDistrictNameTh());
				}
			}
		}

		return custAddressProfile;
	}

}
