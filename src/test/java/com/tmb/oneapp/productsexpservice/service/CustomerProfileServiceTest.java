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
import com.tmb.common.model.address.Province;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
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

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		customerProfileService = new CustomerProfileService(commonServiceClient, customerServiceClient);
	}

	@Test
	public void testCustomerProfileGeneration() {
		TmbOneServiceResponse<CustGeneralProfileResponse> customerModuleResponse = new TmbOneServiceResponse<CustGeneralProfileResponse>();
		CustGeneralProfileResponse profile = new CustGeneralProfileResponse();
		profile.setCitizenId("111115");
		customerModuleResponse.setData(profile);
		TmbOneServiceResponse<List<Province>> provincesRes = new TmbOneServiceResponse();
		List<Province> mockProvice = new ArrayList<Province>();
		provincesRes.setData(mockProvice);
		when(customerServiceClient.getCustomerProfile(any())).thenReturn(ResponseEntity.ok(customerModuleResponse));
		when(commonServiceClient.searchAddressByField(any())).thenReturn(ResponseEntity.ok(provincesRes));
		CustIndividualProfileInfo responseProfile = customerProfileService.getIndividualProfile("1111");
		Assert.assertEquals(profile.getCitizenId(), responseProfile.getCitizenId());
	}

}
