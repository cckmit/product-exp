package com.tmb.oneapp.productsexpservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditCardAccount {
	@JsonProperty("appl_code")
	public String applCode;
	@JsonProperty("acct_ctrl1")
	public String acctCtrl1;
	@JsonProperty("acct_ctrl2")
	public String acctCtrl2;
	@JsonProperty("acct_ctrl3")
	public String acctCtrl3;
	@JsonProperty("acct_ctrl4")
	public String acctCtrl4;
	@JsonProperty("acct_nbr")
	public String acctNbr;
	@JsonProperty("card_number")
	public String cardNumber;
	@JsonProperty("product_group_code")
	public String productGroupCode;
	@JsonProperty("product_group_code_ec")
	public String productGroupCodeEc;
	@JsonProperty("product_code")
	public String productCode;
	@JsonProperty("owner_type")
	public String ownerType;
	@JsonProperty("relationship_code")
	public String relationshipCode;
	@JsonProperty("account_status")
	public String accountStatus;
	@JsonProperty("rm_acct_status")
	public String rmAcctStatus;
	@JsonProperty("xps_acct_status")
	public String xpsAcctStatus;
	@JsonProperty("card_stop_reason")
	public String cardStopReason;
	@JsonProperty("acknowledge_date")
	public String acknowledgeDate;
	@JsonProperty("card_active_flag")
	public String cardActiveFlag;
	@JsonProperty("current_balance")
	public Integer currentBalance;
	@JsonProperty("balance_currency")
	public String balanceCurrency;
	@JsonProperty("purge_flag")
	public String purgeFlag;
	@JsonProperty("create_date")
	public String createDate;
	@JsonProperty("create_by")
	public String createBy;
	@JsonProperty("update_date")
	public String updateDate;
	@JsonProperty("update_by")
	public String updateBy;
	@JsonProperty("previous_exp_date")
	public String previousExpDate;
}
