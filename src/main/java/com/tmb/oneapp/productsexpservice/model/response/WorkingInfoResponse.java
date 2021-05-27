package com.tmb.oneapp.productsexpservice.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.oneapp.productsexpservice.model.flexiloan.CustAddressProfileInfo;
import com.tmb.oneapp.productsexpservice.model.response.lending.WorkProfileInfoResponse;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WorkingInfoResponse {

	@ApiModelProperty(notes = "employmentName")
	@JsonProperty("employmentName")
	private String employmentName;
	@ApiModelProperty(notes = "workingAddress")
	@JsonProperty("workingAddress")
	private CustAddressProfileInfo workingAddress;
	@ApiModelProperty(notes = "workingPhoneNo")
	@JsonProperty("workingPhoneNo")
	private String workingPhoneNo;
	@ApiModelProperty(notes = "workingPhoneNoExt")
	@JsonProperty("workingPhoneNoExt")
	private String workingPhoneNoExt;
	@ApiModelProperty(notes = "profileDependency")
	@JsonProperty("profileDependency")
	private WorkProfileInfoResponse profilesDependency;
	@ApiModelProperty(notes = "professionalCode")
	@JsonProperty("professionalCode")
	private String professionalCode;
	@ApiModelProperty(notes = "incomeDeclared")
	@JsonProperty("incomeDeclared")
	private String incomeDeclared;
	@ApiModelProperty(notes = "incomeBaseSalary")
	@JsonProperty("incomeBaseSalary")
	private String incomeBaseSalary;
	@ApiModelProperty(notes = "incomeDependency")
	@JsonProperty("incomeDependency")
	private DependDefaultEntry incomeDependency;

}
