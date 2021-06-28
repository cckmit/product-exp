package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class CashForYourResponse {

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
