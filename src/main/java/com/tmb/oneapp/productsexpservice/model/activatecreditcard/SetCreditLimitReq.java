package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class SetCreditLimitReq {
	private String mode;
	@JsonProperty("account_id")
	private String accountId;
	@JsonProperty("current_credit_limit")
	private String currentCreditLimit;
	@JsonProperty("effective_date")
	private String effectiveDate;
	@JsonProperty("expiry_date")
	private String expiryDate;
	@JsonProperty("request_reason")
	private String requestReason;
	@JsonProperty("previous_credit_limit")
	private String previousCreditLimit;
	@JsonProperty("type")
	private String type;
	@JsonProperty("reason_desc_en")
	private String reasonDescEn;
	@JsonProperty("englishDes")
	private String reasonDesEn;
	@JsonProperty("reason_desc_th")
	private String reasonDesTh;

}
