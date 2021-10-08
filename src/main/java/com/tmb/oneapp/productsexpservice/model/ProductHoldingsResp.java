package com.tmb.oneapp.productsexpservice.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductHoldingsResp {
	@JsonProperty("saving_accounts")
	public List<SavingAccount> savingAccounts = null;
	@JsonProperty("current_accounts")
	public List<CurrentAccount> currentAccounts = null;
	@JsonProperty("loan_accounts")
	public List<LoanAccount> loanAccounts = null;
	@JsonProperty("trade_finance_accounts")
	public List<Object> tradeFinanceAccounts = null;
	@JsonProperty("treasury_accounts")
	public List<Object> treasuryAccounts = null;
	@JsonProperty("credit_card_accounts")
	public List<CreditCardAccount> creditCardAccounts = null;
	@JsonProperty("debit_card_accounts")
	public List<Object> debitCardAccounts = null;
	@JsonProperty("safety_box_accounts")
	public List<Object> safetyBoxAccounts = null;
	@JsonProperty("hire_purchase_accounts")
	public List<Object> hirePurchaseAccounts = null;
	@JsonProperty("leasing_accounts")
	public List<Object> leasingAccounts = null;
	@JsonProperty("merchant_accounts")
	public List<Object> merchantAccounts = null;
	@JsonProperty("foreign_exchange_accounts")
	public List<Object> foreignExchangeAccounts = null;
	@JsonProperty("mutual_fund_accounts")
	public List<MutualFundAccount> mutualFundAccounts = null;
	@JsonProperty("bancassurance_accounts")
	public List<BancassuranceAccount> bancassuranceAccounts = null;
	@JsonProperty("other_accounts")
	public List<Object> otherAccounts = null;
}
