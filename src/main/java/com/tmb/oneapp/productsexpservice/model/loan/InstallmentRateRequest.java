package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstallmentRateRequest {
	@JsonProperty("groupAccountId")
	private String groupAccountId;
	@JsonProperty("amount")
	private String amount;
	@JsonProperty("billCycleCutDate")
	private String billCycleCutDate;
	@JsonProperty("disbursementDate")
	private String disbursementDate;
	@JsonProperty("promoSegment")
	private String promoSegment;
	@JsonProperty("cashChillChillFlag")
	private String cashChillChillFlag;
	@JsonProperty("cashTransferFlag")
	private String cashTransferFlag;
	@JsonProperty("getAllDetailFlag")
	private String getAllDetailFlag;
}