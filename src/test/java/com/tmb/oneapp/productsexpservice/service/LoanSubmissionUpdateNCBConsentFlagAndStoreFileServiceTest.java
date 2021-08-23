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
import com.tmb.oneapp.productsexpservice.model.response.lending.CustomerInformationResponse;
import com.tmb.oneapp.productsexpservice.model.response.lending.UpdateNCBConsentFlagRequest;

@RunWith(JUnit4.class)
public class LoanSubmissionUpdateNCBConsentFlagAndStoreFileServiceTest {

	@Mock
	private LendingServiceClient lendingServiceClient;

	LoanSubmissionUpdateNCBConsentFlagAndStoreFileService loanSubmissionUpdateNCBConsentFlagAndStoreFileService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		loanSubmissionUpdateNCBConsentFlagAndStoreFileService = new LoanSubmissionUpdateNCBConsentFlagAndStoreFileService(
				lendingServiceClient);
	}

	@Test
	void testUpdateNCBConsentFlagAndStoreFile() throws Exception {

		CustomerInformationResponse customerInfoRes = new CustomerInformationResponse();
		TmbOneServiceResponse<CustomerInformationResponse> oneServiceResponse = new TmbOneServiceResponse<>();
		oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "success", "lending-service"));
		oneServiceResponse.setData(customerInfoRes);
		when(lendingServiceClient.updateNCBConsentFlagAndStoreFile(any(), any(), any()))
				.thenReturn(ResponseEntity.ok(oneServiceResponse));

		Assert.assertNotNull(loanSubmissionUpdateNCBConsentFlagAndStoreFileService
				.updateNCBConsentFlagAndStoreFile("correlationId", "crmid", new UpdateNCBConsentFlagRequest()));
	}

}
