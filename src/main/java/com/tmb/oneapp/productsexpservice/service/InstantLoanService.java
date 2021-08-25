package com.tmb.oneapp.productsexpservice.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.loan.InstantLoanCreationRequest;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;

@Service
public class InstantLoanService {

	private LendingServiceClient lendingServiceClient;

	public InstantLoanService(LendingServiceClient lendingServiceClient) {
		this.lendingServiceClient = lendingServiceClient;
	}

	/**
	 * Proxy warper
	 * 
	 * @param reqHeaders
	 * @param request
	 * @return
	 */
	public Object createInstanceLoanApplication(Map<String, String> reqHeaders, InstantLoanCreationRequest request) {
		ResponseEntity<TmbOneServiceResponse<Object>> response = lendingServiceClient
				.createInstanceLoanApplication(reqHeaders, request);
		return response.getBody().getData();
	}

}
