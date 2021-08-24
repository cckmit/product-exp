package com.tmb.oneapp.productsexpservice.model.flexiloan;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
	@ApiModelProperty(notes = "nationality", example = "TH")
	@JsonProperty("nationality")
	private String nationality;
	@ApiModelProperty(notes = "nationalityLabel")
	@JsonProperty("nationalityLabel")
	private String nationalityLabel;
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
	@ApiModelProperty(notes = "adddressDetail")
	private CustAddressProfileInfo address;
	
	@ApiModelProperty(notes = "idType")
	@JsonProperty("idType")
	private String idType;
	@ApiModelProperty(notes = "idNo")
	@JsonProperty("idNo")
	private String idNo;
	@ApiModelProperty(notes = "rmNoId")
	@JsonProperty("rmNoId")
	private String rmNoId;
	@ApiModelProperty(notes = "firstNameTh")
	@JsonProperty("firstNameTh")
	private String firstNameTh;
	@ApiModelProperty(notes = "middleNameTh")
	@JsonProperty("middleNameTh")
	private String middleNameTh;
	@ApiModelProperty(notes = "lastNameTh")
	@JsonProperty("lastNameTh")
	private String lastNameTh;
	@JsonProperty("email")
	private String email;
	
	
}
