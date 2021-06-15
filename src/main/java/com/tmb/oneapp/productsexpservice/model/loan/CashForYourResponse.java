package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CashForYourResponse {

	@JsonProperty("status")
	private Status status;
	@JsonProperty("installment_data")
	private InstallmentData installmentData;
	@JsonProperty("cash_fee_rate")
	private String cashFeeRate;
	@JsonProperty("cash_interest_rate")
	private String cashInterestRate;
	@JsonProperty("cash_vat_rate")
	private String cashVatRate;
	@JsonProperty("max_transfer_amt")
	private String maximumTransferAmt;

}
