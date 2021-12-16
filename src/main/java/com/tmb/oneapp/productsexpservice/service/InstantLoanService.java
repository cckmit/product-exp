package com.tmb.oneapp.productsexpservice.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.loan.InstantLoanCreationRequest;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
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
	 * @param oneServiceResponse 
	 * @return
	 */
	public void createInstanceLoanApplication(Map<String, String> reqHeaders, InstantLoanCreationRequest request, TmbOneServiceResponse<Object> oneServiceResponse) {
		String crmId = reqHeaders.get(ProductsExpServiceConstant.X_CRMID);
		String correlationId = reqHeaders.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID);
		ResponseEntity<TmbOneServiceResponse<Object>> response = lendingServiceClient
				.createInstanceLoanApplication(correlationId, crmId, request);
		oneServiceResponse.setStatus(response.getBody().getStatus());
		oneServiceResponse.setData(response.getBody().getData());
	}

}
