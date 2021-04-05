package com.tmb.oneapp.productsexpservice.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MonthlyTrans {
	private BigDecimal totalInterest;
	private BigDecimal principle;
	private BigDecimal principle2Digit;
	private BigDecimal interest;
	private BigDecimal interest2Digit;
	private BigDecimal firstPayment;
	private BigDecimal totalAmt;
}
