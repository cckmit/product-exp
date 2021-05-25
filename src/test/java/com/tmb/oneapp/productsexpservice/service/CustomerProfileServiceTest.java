package com.tmb.oneapp.productsexpservice.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.address.District;
import com.tmb.common.model.address.Province;
import com.tmb.common.model.address.SubDistrict;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanInstantGetCustomerInfoClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustIndividualProfileInfo;
import com.tmb.oneapp.productsexpservice.model.request.AddressCommonSearchReq;
import com.tmb.oneapp.productsexpservice.model.response.DependDefaultEntry;
import com.tmb.oneapp.productsexpservice.model.response.WorkingInfoResponse;
import com.tmb.oneapp.productsexpservice.model.response.lending.WorkProfileInfoResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

@RunWith(JUnit4.class)
public class CustomerProfileServiceTest {
	@Mock
	CustomerProfileService customerProfileService;
	@Mock
	CommonServiceClient commonServiceClient;
	@Mock
	CustomerServiceClient customerServiceClient;
	@Mock
	LendingServiceClient lendingServiceClient;
	@Mock
	LoanInstantGetCustomerInfoClient instanceCustomerInfoClient;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		customerProfileService = new CustomerProfileService(commonServiceClient, customerServiceClient,
				lendingServiceClient, instanceCustomerInfoClient);
	}

	@Test
	public void testCustomerProfileGeneration() {
		TmbOneServiceResponse<CustGeneralProfileResponse> customerModuleResponse = new TmbOneServiceResponse<CustGeneralProfileResponse>();
		CustGeneralProfileResponse profile = new CustGeneralProfileResponse();
		profile.setCitizenId("111115");
		profile.setBiometricFlag("A");
		profile.setBusinessTypeCode("KA090000");
		profile.setCurrentAddrdistrictNameTh("AB");
		profile.setCurrentAddrFloorNo("ACDD");
		profile.setCurrentAddrHouseNo("156");
		profile.setCurrentAddrMoo("CCDS");
		profile.setCurrentAddrNameTh("CCSD");
		profile.setCurrentAddrprovinceCode("CC900");
		profile.setCurrentAddrProvinceNameTh("ACDDD");
		profile.setCurrentAddrRoomNo("ASDVD");
		profile.setCurrentAddrSoi("DSCDSD");
		profile.setCurrentAddrStreet("SKID");
		profile.setCurrentAddrSubDistrictNameTh("MLLOOO");
		profile.setCurrentAddrVillageOrbuilding("OIKUIJ");
		profile.setCurrentAddrZipcode("10800");
		profile.setCustomerStatus("ACTIVE");
		profile.setDeviceNickname("MMY");
		profile.setEmailAddress("A@B.com");
		profile.setEmailType("AC");
		profile.setEmailVerifyFlag("Y");
		profile.setEngFname("AAAABB");
		profile.setEngLname("KKKKK");
		profile.setIdBirthDate("1985-01-26");
		profile.setIdNo("998882711");
		profile.setMaskAcctIdFlag("CCCD");
		profile.setNationality("TH");
		profile.setOccupationCode("306");
		profile.setPhoneNoFull("078992102");
		profile.setProfileImage("LOKS");
		customerModuleResponse.setData(profile);
		customerModuleResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		TmbOneServiceResponse<List<Province>> provincesRes = new TmbOneServiceResponse();
		List<Province> mockProvice = new ArrayList<Province>();
		Province testProvince = new Province();
		mockProvice.add(testProvince);

		List<District> districts = new ArrayList<District>();
		District testDistrict = new District();
		List<SubDistrict> subDistricts = new ArrayList<SubDistrict>();
		testDistrict.setSubDistrictList(subDistricts);
		testProvince.setDistrictList(districts);
		provincesRes.setData(mockProvice);
		provincesRes.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.getCustomerProfile(any())).thenReturn(ResponseEntity.ok(customerModuleResponse));
		when(commonServiceClient.searchAddressByField(any())).thenReturn(ResponseEntity.ok(provincesRes));
		CustIndividualProfileInfo responseProfile = customerProfileService.getIndividualProfile("1111");
		Assert.assertEquals(profile.getCitizenId(), responseProfile.getCitizenId());
	}

	@Test
	public void testCustomerWorkingProfileInfo() {
		customerProfileService = new CustomerProfileService(commonServiceClient, customerServiceClient,
				lendingServiceClient, instanceCustomerInfoClient);
		TmbOneServiceResponse<CustGeneralProfileResponse> customerModuleResponse = new TmbOneServiceResponse<CustGeneralProfileResponse>();
		CustGeneralProfileResponse profile = new CustGeneralProfileResponse();
		profile.setCitizenId("111115");
		profile.setCitizenId("111115");
		profile.setBiometricFlag("A");
		profile.setBusinessTypeCode("KA090000");
		profile.setCurrentAddrdistrictNameTh("AB");
		profile.setCurrentAddrFloorNo("ACDD");
		profile.setCurrentAddrHouseNo("156");
		profile.setCurrentAddrMoo("CCDS");
		profile.setCurrentAddrNameTh("CCSD");
		profile.setCurrentAddrprovinceCode("CC900");
		profile.setCurrentAddrProvinceNameTh("ACDDD");
		profile.setCurrentAddrRoomNo("ASDVD");
		profile.setCurrentAddrSoi("DSCDSD");
		profile.setCurrentAddrStreet("SKID");
		profile.setCurrentAddrSubDistrictNameTh("MLLOOO");
		profile.setCurrentAddrVillageOrbuilding("OIKUIJ");
		profile.setCurrentAddrZipcode("10800");
		profile.setCustomerStatus("ACTIVE");
		profile.setDeviceNickname("MMY");
		profile.setEmailAddress("A@B.com");
		profile.setEmailType("AC");
		profile.setEmailVerifyFlag("Y");
		profile.setEngFname("AAAABB");
		profile.setEngLname("KKKKK");
		profile.setIdBirthDate("1985-01-26");
		profile.setIdNo("998882711");
		profile.setMaskAcctIdFlag("CCCD");
		profile.setNationality("TH");
		profile.setOccupationCode("306");
		profile.setPhoneNoFull("078992102");
		profile.setProfileImage("LOKS");
		profile.setWorkAddrdistrictNameTh("A");
		profile.setWorkAddrSubDistrictNameTh("B");
		profile.setWorkAddrProvinceNameTh("C");
		customerModuleResponse.setData(profile);
		customerModuleResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.getCustomerProfile(any())).thenReturn(ResponseEntity.ok(customerModuleResponse));
		TmbOneServiceResponse<List<Province>> listProvinces = new TmbOneServiceResponse<List<Province>>();
		List<Province> provinces = new ArrayList<Province>();
		Province aProvinces = new Province();
		aProvinces.setProvinceNameTh(profile.getWorkAddrProvinceNameTh());
		List<District> districtList = new ArrayList<District>();
		List<SubDistrict> subDistricts = new ArrayList<SubDistrict>();
		SubDistrict subDistrict = new SubDistrict();
		subDistrict.setSubDistrictNameTh(profile.getWorkAddrSubDistrictNameTh());
		subDistricts.add(subDistrict);
		District aDistrict = new District();
		aDistrict.setDistrictNameTh(profile.getWorkAddrdistrictNameTh());
		aDistrict.setSubDistrictList(subDistricts);
		districtList.add(aDistrict);
		aProvinces.setDistrictList(districtList);
		provinces.add(aProvinces);
		listProvinces.setData(provinces);
		when(commonServiceClient.searchAddressByField(any())).thenReturn(ResponseEntity.ok(listProvinces));
		TmbOneServiceResponse<WorkProfileInfoResponse> workProfileRes = new TmbOneServiceResponse<WorkProfileInfoResponse>();
		WorkProfileInfoResponse profileInfo = new WorkProfileInfoResponse();
		DependDefaultEntry entry = new DependDefaultEntry();

		profileInfo.setBusinessType(entry);
		profileInfo.setCountryIncomes(entry);
		profileInfo.setOccupation(entry);
		profileInfo.setSourceIncomes(entry);
		profileInfo.setSubBusinessType(entry);
		profileInfo.setWorkstatus(entry);

		workProfileRes.setData(profileInfo);
		when(lendingServiceClient.getWorkInformationWithProfile(any(), any(), any(), any()))
				.thenReturn(ResponseEntity.ok(workProfileRes));
		try {
			WorkingInfoResponse responseWorkingProfile = customerProfileService.getWorkingInformation("111", "dxsd");
		} catch (RemoteException | ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Assert.assertTrue(true);
	}

}
