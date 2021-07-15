package com.tmb.oneapp.productsexpservice.model.applyestatement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "appl_code", "acct_ctrl1", "acct_ctrl2", "acct_ctrl3", "acct_ctrl4", "acct_nbr", "account_name",
		"product_group_code", "product_code", "owner_type", "relationship_code", "account_status", "current_balance",
		"balance_currency", "account_open_dt", "rm_account_status", "xps_account_status" })
public class SavingAccountETEResp {

	@JsonProperty("appl_code")
	private String applCode;
	@JsonProperty("acct_ctrl1")
	private String acctCtrl1;
	@JsonProperty("acct_ctrl2")
	private String acctCtrl2;
	@JsonProperty("acct_ctrl3")
	private String acctCtrl3;
	@JsonProperty("acct_ctrl4")
	private String acctCtrl4;
	@JsonProperty("acct_nbr")
	private String acctNbr;
	@JsonProperty("account_name")
	private String accountName;
	@JsonProperty("product_group_code")
	private String productGroupCode;
	@JsonProperty("product_code")
	private String productCode;
	@JsonProperty("owner_type")
	private String ownerType;
	@JsonProperty("relationship_code")
	private String relationshipCode;
	@JsonProperty("account_status")
	private String accountStatus;
	@JsonProperty("current_balance")
	private Double currentBalance;
	@JsonProperty("balance_currency")
	private String balanceCurrency;
	@JsonProperty("account_open_dt")
	private String accountOpenDt;
	@JsonProperty("rm_account_status")
	private String rmAccountStatus;
	@JsonProperty("xps_account_status")
	private String xpsAccountStatus;
}
