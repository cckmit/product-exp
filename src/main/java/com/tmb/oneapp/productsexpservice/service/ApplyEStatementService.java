package com.tmb.oneapp.productsexpservice.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.applyestatement.ApplyEStatementResponse;

@Service
public class ApplyEStatementService {

	private CustomerServiceClient customerServiceClient;

	public ApplyEStatementService(CustomerServiceClient customerServiceClient) {
		this.customerServiceClient = customerServiceClient;
	}

	/**
	 * get e-statement
	 * 
	 * @param correlationId
	 * @param crmId
	 * @return
	 */
	public ApplyEStatementResponse getEStatement(String crmId, String correlationId) {
		ResponseEntity<TmbOneServiceResponse<ApplyEStatementResponse>> result = customerServiceClient
				.getCustomerEStatement(crmId, correlationId);
		return result.getBody().getData();
	}

}
