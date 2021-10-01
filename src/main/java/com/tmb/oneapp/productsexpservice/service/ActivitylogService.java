package com.tmb.oneapp.productsexpservice.service;

import org.springframework.stereotype.Service;

import com.tmb.common.model.customer.UpdateEStatmentRequest;

@Service
public class ActivitylogService {

	private CreditCardLogService creditCardLogService;

	public ActivitylogService(CreditCardLogService creditCardLogService) {
		this.creditCardLogService = creditCardLogService;
	}

	public void updatedEStatmentCard(UpdateEStatmentRequest updateEstatementReq, boolean result, String errorCode) {
		// TODO Auto-generated method stub

	}

	public void updatedEStatmentLoan(UpdateEStatmentRequest updateEstatementReq, boolean result, String errorCode) {
		// TODO Auto-generated method stub

	}

}
