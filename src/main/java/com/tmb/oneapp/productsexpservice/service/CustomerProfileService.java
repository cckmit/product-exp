package com.tmb.oneapp.productsexpservice.service;

import org.springframework.stereotype.Service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustIndividualProfileInfo;

/**
 * Customer Profile Service
 * 
 * @author Witsanu
 *
 */
@Service
public class CustomerProfileService {

	private static final TMBLogger<CustomerProfileService> logger = new TMBLogger<>(CustomerProfileService.class);

	public CustomerProfileService(CommonServiceClient commonServiceClient,
			CustomerServiceClient customerServiceClient) {
		// TODO Auto-generated constructor stub
	}

	public CustIndividualProfileInfo getIndividualProfile() {
		CustIndividualProfileInfo profileInfo = new CustIndividualProfileInfo();
		return null;
	}

}
