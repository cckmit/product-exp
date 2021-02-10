package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CardBalances {
	private BigDecimal currentBalance;
	private BigDecimal ledgerBalance;
	private BigDecimal availableCashAdvance;
	private BigDecimal availableCreditAllowance;
	private BigDecimal lastPaymentAmount;
	private String lastPaymentDate;
	private BigDecimal eptbInstallmentAvailable;
	private BigDecimal epaiInstallmentAvailable;
	private BalanceCredit balanceCreditLimit;
	private BalanceCredit balanceCreditLine;
}
