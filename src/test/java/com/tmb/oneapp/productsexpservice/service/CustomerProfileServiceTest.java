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
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustIndividualProfileInfo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		customerProfileService = new CustomerProfileService(commonServiceClient, customerServiceClient,
				lendingServiceClient);
	}

	@Test
	public void testCustomerProfileGeneration() {
		TmbOneServiceResponse<CustGeneralProfileResponse> customerModuleResponse = new TmbOneServiceResponse<CustGeneralProfileResponse>();
		CustGeneralProfileResponse profile = new CustGeneralProfileResponse();
		profile.setCitizenId("111115");
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
		TmbOneServiceResponse<CustGeneralProfileResponse> customerModuleResponse = new TmbOneServiceResponse<CustGeneralProfileResponse>();
		CustGeneralProfileResponse profile = new CustGeneralProfileResponse();
		profile.setCitizenId("111115");
		customerModuleResponse.setData(profile);
		customerModuleResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
				ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
		when(customerServiceClient.getCustomerProfile(any())).thenReturn(ResponseEntity.ok(customerModuleResponse));
	}

}
