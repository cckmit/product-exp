package com.tmb.oneapp.productsexpservice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.customer.UpdateEStatmentRequest;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.applyestatement.ApplyEStatementResponse;

@Service
public class ApplyEStatementService {

	private static final TMBLogger<ApplyEStatementService> logger = new TMBLogger<>(ApplyEStatementService.class);
	private CustomerServiceClient customerServiceClient;
	private CreditCardClient creditCardClient;

	public ApplyEStatementService(CustomerServiceClient customerServiceClient, CreditCardClient creditCardClient) {
		this.customerServiceClient = customerServiceClient;
		this.creditCardClient = creditCardClient;
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

	/**
	 * Update e statement by crm id
	 * 
	 * @param crmId
	 * @param correlationId
	 * @param updateEstatementReq
	 * @throws TMBCommonException 
	 */
	public void updateEstatement(String crmId, String correlationId, UpdateEStatmentRequest updateEstatementReq) throws TMBCommonException {
		ApplyEStatementResponse currentEstatementResponse = getEStatement(crmId, correlationId);
		if ("Y".equals(currentEstatementResponse.getCustomer().getStatementFlag().getECreditcardStatementFlag())) {
			logger.info("This rm already apply e statment completed");
			return;
		}
		updateEStatementOnSilverLake(crmId, correlationId, updateEstatementReq);
		
		try {
			Map<String, String> requestHeaders = new HashMap<String, String>();
			requestHeaders.put(ProductsExpServiceConstant.X_CORRELATION_ID, correlationId);
			requestHeaders.put(ProductsExpServiceConstant.X_CRMID, crmId);
			ResponseEntity<TmbOneServiceResponse<ApplyEStatementResponse>> response = customerServiceClient
					.updateEStatement(requestHeaders);
			if(!ResponseCode.SUCESS.getCode().equals(response.getBody().getStatus().getCode())) {
				throw new TMBCommonException("Fail on update EC system");
			}
		} catch (Exception e) {
			logger.error(e.toString(),e);
			rollBackSilverlake(crmId, correlationId, updateEstatementReq);
			throw new TMBCommonException(e.getMessage());
		}
	}
	
	private void rollBackSilverlake(String crmId, String correlationId, UpdateEStatmentRequest updateEstatementReq) {
		logger.info("### ROLL BACK SILVERLAKE FOR {} ### ",crmId);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(ProductsExpServiceConstant.X_CORRELATION_ID, correlationId);
		headers.put(ProductsExpServiceConstant.X_CRMID, crmId);
		creditCardClient.cancelEnableEStatement(headers);
	}

	/**
	 * Update e statment on silverlake
	 * @param crmId
	 * @param correlationId
	 * @param updateEstatementReq
	 */
	private void updateEStatementOnSilverLake(String crmId, String correlationId,
			UpdateEStatmentRequest updateEstatementReq) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(ProductsExpServiceConstant.X_CORRELATION_ID, correlationId);
		headers.put(ProductsExpServiceConstant.X_CRMID, crmId);
		creditCardClient.updateEmailEStatement(headers, updateEstatementReq);
		creditCardClient.updateEnableEStatement(headers, updateEstatementReq);
	}

}
