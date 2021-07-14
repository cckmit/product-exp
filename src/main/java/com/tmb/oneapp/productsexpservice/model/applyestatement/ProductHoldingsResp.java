package com.tmb.oneapp.productsexpservice.model.applyestatement;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductHoldingsResp {
	@JsonProperty("saving_accounts")
	private List<SavingAccountETEResp> savingAccounts;
	@JsonProperty("current_accounts")
	private List<SavingAccountETEResp> currentAccounts;
	@JsonProperty("loan_accounts")
	private List<Object> loanAccounts;
	@JsonProperty("trade_finance_accounts")
	private List<Object> tradeFinanceAccounts;
	@JsonProperty("treasury_accounts")
	private List<Object> treasuryAccounts;
	@JsonProperty("credit_card_accounts")
	private List<Object> creditCardAccounts;
	@JsonProperty("debit_card_accounts")
	private List<Object> debitCardAccounts;
	@JsonProperty("safety_box_accounts")
	private List<Object> safetyBoxAccounts;
	@JsonProperty("hire_purchase_accounts")
	private List<SavingAccountETEResp> hirePurchaseAccounts;
	@JsonProperty("leasing_accounts")
	private List<Object> leasingAccounts;
	@JsonProperty("merchant_accounts")
	private List<Object> merchantAccounts;
	@JsonProperty("foreign_exchange_accounts")
	private List<Object> foreignExchangeAccounts;
	@JsonProperty("mutual_fund_accounts")
	private List<Object> mutualFundAccounts;
	@JsonProperty("bancassurance_accounts")
	private List<Object> bancassuranceAccounts;
	@JsonProperty("other_accounts")
	private List<Object> otherAccounts;
}
