package com.tmb.oneapp.productsexpservice.service;

import org.springframework.stereotype.Service;

@Service
public class ActivitylogService {

	private CreditCardLogService creditCardLogService;

	public ActivitylogService(CreditCardLogService creditCardLogService) {
		this.creditCardLogService = creditCardLogService;
	}

	public void updatedEStatmentCard(String accountId, boolean result) {
		// TODO Auto-generated method stub

	}

	public void updatedEStatmentLoan(String loanId, boolean result) {
		// TODO Auto-generated method stub

	}

}
