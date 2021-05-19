package com.tmb.oneapp.productsexpservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustAddressProfileInfo;
import com.tmb.oneapp.productsexpservice.model.response.lending.WorkProfileInfoResponse;

import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class WorkingInfoResponse {

	@JsonProperty("employmentName")
	private String employmentName;
	@JsonProperty("workingAddress")
	private CustAddressProfileInfo workingAddress;
	@JsonProperty("workingPhoneNo")
	private String workingPhoneNo;
	@JsonProperty("workingPhoneNoExt")
	private String workingPhoneNoExt;
	@JsonProperty("profileDependency")
	private WorkProfileInfoResponse profilesDependency;

}
