package com.tmb.oneapp.productsexpservice.service;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;

@RunWith(JUnit4.class)
public class InstantLoanServiceTest {

	@Mock
	LendingServiceClient lendingServiceClient;

	private InstantLoanService instanceLoanSevice;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		instanceLoanSevice = new InstantLoanService(lendingServiceClient);
	}

	@Test
	public void test() {
		TmbOneServiceResponse<Object> serviceResponse = new TmbOneServiceResponse<Object>();
		when(lendingServiceClient.createInstanceLoanApplication(any(), any(), any()))
				.thenReturn(ResponseEntity.ok().body(serviceResponse));
		Object obj = instanceLoanSevice.createInstanceLoanApplication(new HashMap<String, String>(), null);
		assertNull(obj);
	}

}
