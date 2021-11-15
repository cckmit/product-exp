package com.tmb.oneapp.productsexpservice.model.loan;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(Include.NON_NULL)
@ToString
public class CashForYourResponse {

	@JsonProperty("installment_data")
	private InstallmentData installmentData;
	@JsonProperty("cash_fee_rate")
	private String cashFeeRate;
	@JsonProperty("cash_interest_rate")
	private String cashInterestRate;
	@JsonProperty("cash_vat_rate")
	private String cashVatRate;
	@JsonProperty("cash_vat_total")
	private String cashVatTotal;
	@JsonProperty("max_transfer_amt")
	private String maximumTransferAmt;

	@JsonProperty("fee_cash_transfer")
	private String feeCashTransfer;
	@JsonProperty("vat_cash_transfer")
	private String vatCashTransfer;
	
	@JsonProperty("fee_cash_chill_chill")
	private String feeCashChillChill;
	@JsonProperty("vat_cash_chill_chill")
	private String vatCashChillChill;
	@JsonProperty("none_flash_month")
	private String noneFlashMonth;
	@JsonProperty("eff_rate_prducts")
	private List<String> effRateProducts;
	
}
