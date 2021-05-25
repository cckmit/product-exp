package com.tmb.oneapp.productsexpservice.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MonthlyTrans {
	private BigDecimal totalInterest = BigDecimal.ZERO;
	private BigDecimal principle = BigDecimal.ZERO;
	private BigDecimal principle2Digit = BigDecimal.ZERO;
	private BigDecimal interest = BigDecimal.ZERO;
	private BigDecimal interest2Digit = BigDecimal.ZERO;
	private BigDecimal firstPayment = BigDecimal.ZERO;
	private BigDecimal totalAmt = BigDecimal.ZERO;
}
