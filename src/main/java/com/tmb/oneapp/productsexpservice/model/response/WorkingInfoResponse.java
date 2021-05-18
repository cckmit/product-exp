package com.tmb.oneapp.productsexpservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustAddressProfileInfo;

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
	@JsonProperty("workstatus")
	private DependDefaultEntry workstatus;
	@JsonProperty("occupation")
	private DependDefaultEntry occupation;
	@JsonProperty("businessType")
	private DependDefaultEntry businessType;
	@JsonProperty("subBusinessType")
	private DependDefaultEntry subBusinessType;
	@JsonProperty("sourceIncomes")
	private DependDefaultEntry sourceIncomes;
	@JsonProperty("countryIncomes")
	private DependDefaultEntry countryIncomes;

}
