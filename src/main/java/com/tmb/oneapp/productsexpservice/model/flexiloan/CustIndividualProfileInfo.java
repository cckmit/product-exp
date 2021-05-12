package com.tmb.oneapp.productsexpservice.model.flexiloan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CustIndividualProfileInfo {

	@ApiModelProperty(notes = "customerFullTH")
	@JsonProperty("customerFullTH")
	private String customerFullTh;
	@ApiModelProperty(notes = "customerFullEN", example = "Witsanu Thammayon")
	@JsonProperty("customerFullEN")
	private String customerFullEN;
	@ApiModelProperty(notes = "birthdate", example = "1986-05-08")
	@JsonProperty("birthdate")
	private String birthdate;
	@ApiModelProperty(notes = "citizenId", example = "1180200031669")
	@JsonProperty("citizenId")
	private String citizenId;
	@ApiModelProperty(notes = "expireDate", example = "2022-05-08")
	@JsonProperty("expireDate")
	private String expireDate;
	@ApiModelProperty(notes = "nationality", example = "thai")
	@JsonProperty("nationality")
	private String nationality;
	@ApiModelProperty(notes = "mobileNo", example = "0840708099")
	@JsonProperty("mobileNo")
	private String mobileNo;
	@ApiModelProperty(notes = "remark", example = "")
	@JsonProperty("remark")
	private String remark;
	@ApiModelProperty(notes = "adddressInfo")
	@JsonProperty("adddressInfo")
	private String adddressInfo;
	@JsonProperty("address")
	private CustAddressProfileInfo address;
}
