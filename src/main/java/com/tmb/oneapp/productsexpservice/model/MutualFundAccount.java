package com.tmb.oneapp.productsexpservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MutualFundAccount {
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
	@JsonProperty("product_group_code")
	public String productGroupCode;
	@JsonProperty("product_group_code_ec")
	public String productGroupCodeEc;
	@JsonProperty("product_code")
	public String productCode;
	@JsonProperty("relationship_code")
	public String relationshipCode;
	@JsonProperty("account_open_dt")
	public String accountOpenDt;
	@JsonProperty("account_status")
	public String accountStatus;
	@JsonProperty("rm_account_status")
	public String rmAccountStatus;
	@JsonProperty("xps_account_status")
	public String xpsAccountStatus;
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
}
