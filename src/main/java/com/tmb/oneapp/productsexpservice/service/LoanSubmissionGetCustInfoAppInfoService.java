package com.tmb.oneapp.productsexpservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.response.lending.CustomerInfoApplicationInfo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LoanSubmissionGetCustInfoAppInfoService {
	private static final TMBLogger<LoanSubmissionGetCustInfoAppInfoService> logger = new TMBLogger<>(
			LoanSubmissionGetCustInfoAppInfoService.class);
	private final LendingServiceClient lendingServiceClient;

	public CustomerInfoApplicationInfo getCustomerInfoApplicationInfo(String correlationId, String crmId, String caId)
			throws TMBCommonException {
		try {
			TmbOneServiceResponse<CustomerInfoApplicationInfo> responseEntity = lendingServiceClient
					.getCustomerInfoAndApplicationInfo(correlationId, crmId, caId).getBody();
			if (responseEntity.getStatus().getCode().equals(ResponseCode.SUCESS.getCode())) {
				return responseEntity.getData();
			} else {
				throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
						ResponseCode.FAILED.getService(), HttpStatus.NOT_FOUND, null);
			}
		} catch (Exception e) {
			logger.error("getCustomerInfoApplicationInfo got exception:{}", e);
			throw e;
		}
	}
}
