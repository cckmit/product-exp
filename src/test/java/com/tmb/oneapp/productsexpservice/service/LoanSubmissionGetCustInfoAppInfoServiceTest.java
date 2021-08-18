package com.tmb.oneapp.productsexpservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.response.lending.CustomerInfoApplicationInfo;

@RunWith(JUnit4.class)
public class LoanSubmissionGetCustInfoAppInfoServiceTest {

	@Mock
	private LendingServiceClient lendingServiceClient;
	@Mock
	LoanSubmissionGetCustInfoAppInfoService loanSubmissionGetCustInfoAppInfoService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		loanSubmissionGetCustInfoAppInfoService = new LoanSubmissionGetCustInfoAppInfoService(lendingServiceClient);
	}

	@Test
	void testGetCustomerInfoAndApplicationInfo() throws Exception {

		CustomerInfoApplicationInfo customerInfoApplicationInfo = new CustomerInfoApplicationInfo();
		TmbOneServiceResponse<CustomerInfoApplicationInfo> oneServiceResponse = new TmbOneServiceResponse<>();
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
		oneServiceResponse.setData(customerInfoApplicationInfo);
		when(lendingServiceClient.getCustomerInfoAndApplicationInfo(any(), any(), any()))
				.thenReturn(ResponseEntity.ok(oneServiceResponse));

		Assert.assertNotNull(loanSubmissionGetCustInfoAppInfoService.getCustomerInfoApplicationInfo("correlationId",
				"crmid", "caId"));
	}

}
