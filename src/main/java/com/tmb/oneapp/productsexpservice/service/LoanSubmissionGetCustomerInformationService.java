package com.tmb.oneapp.productsexpservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.response.lending.CustomerInformationResponse;
import com.tmb.oneapp.productsexpservice.model.response.lending.UpdateNCBConsentFlagRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LoanSubmissionGetCustomerInformationService {
	private static final TMBLogger<LoanSubmissionGetCustomerInformationService> logger = new TMBLogger<>(
			LoanSubmissionGetCustomerInformationService.class);
	private final LendingServiceClient lendingServiceClient;

	public CustomerInformationResponse getCustomerInformation(String correlationId, String crmId,
			UpdateNCBConsentFlagRequest request) throws TMBCommonException {
		try {
			TmbOneServiceResponse<CustomerInformationResponse> responseEntity = lendingServiceClient
					.getCustomerInformation(correlationId, crmId, request).getBody();
			if (responseEntity.getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
				return responseEntity.getData();
			} else {
				throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
						ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
			}
		} catch (Exception e) {
			logger.error("getCustomerInformation got exception:{}", e);
			throw e;
		}
	}
}
