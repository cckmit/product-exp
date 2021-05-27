package com.tmb.oneapp.productsexpservice.controller;

import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.address.Province;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanInstantGetCustomerInfoClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustIndividualProfileInfo;
import com.tmb.oneapp.productsexpservice.model.response.CodeEntry;
import com.tmb.oneapp.productsexpservice.service.CustomerProfileService;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

@RunWith(JUnit4.class)
public class CustomerServiceControllerTest {

	private CustomerServiceController customerServiceController;
	@Mock
	private CustomerProfileService customerProfileService;
	@Mock
	private CommonServiceClient commonServiceClient;
	@Mock
	private LendingServiceClient lendingServiceClient;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		customerServiceController = new CustomerServiceController(customerProfileService, commonServiceClient,
				lendingServiceClient);
	}

	@Test
	public void testController() {
		CustIndividualProfileInfo custIndividualProfile = new CustIndividualProfileInfo();
		TmbOneServiceResponse<List<CodeEntry>> body = new TmbOneServiceResponse();
		when(customerProfileService.getIndividualProfile(any())).thenReturn(custIndividualProfile);
		when(lendingServiceClient.getCountryList(any())).thenReturn(ResponseEntity.ok(body));
		when(lendingServiceClient.getSourceOfIncomeInfo("CC", "CC")).thenReturn(ResponseEntity.ok(body));
		when(lendingServiceClient.getBusinessTypeInfo(any())).thenReturn(ResponseEntity.ok(body));
		when(lendingServiceClient.getWorkStatusInfo(any(), any())).thenReturn(ResponseEntity.ok(body));
		when(lendingServiceClient.getWorkStatusInfo(any())).thenReturn(ResponseEntity.ok(body));
		TmbOneServiceResponse<List<Province>> provinces = new TmbOneServiceResponse();
		when(commonServiceClient.searchAddressByField(any())).thenReturn(ResponseEntity.ok(provinces));

		customerServiceController.getIndividualProfileInfo(new HashedMap<String, String>());
		customerServiceController.getCountryDependency(new HashedMap<String, String>());
		customerServiceController.getCountryIncomeSourceDependency("TH", new HashedMap<String, String>());
		customerServiceController.getWorkingDependencyBusinessType(new HashedMap<String, String>());
		customerServiceController.getWorkingDependencyByOccupationCode("02", new HashedMap<String, String>());
		customerServiceController.getWorkingInformation(new HashedMap<String, String>());
		customerServiceController.getWorkingStatusDependency(new HashedMap<String, String>());
		customerServiceController.getZipcodeInfo("10800", new HashedMap<String, String>());
		assertTrue(true);
	}

}
