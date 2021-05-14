package com.tmb.oneapp.productsexpservice.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class WorkingInfoReq {

	@JsonProperty("occupationCode")
	private String occupationCode;
	@JsonProperty("businessTypeCode")
	private String businessTypeCode;
	@JsonProperty("sciContryCode")
	private String sciContryCode;
	@JsonProperty("countryOfIncomeCode")
	private String countryOfIncomeCode;

}
