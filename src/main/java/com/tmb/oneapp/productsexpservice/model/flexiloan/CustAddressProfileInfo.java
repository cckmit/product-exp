package com.tmb.oneapp.productsexpservice.model.flexiloan;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustAddressProfileInfo {

	@ApiModelProperty(notes = "houseNo", example = "156")
	@JsonProperty("houseNo")
	private String houseNo;
	@ApiModelProperty(notes = "roomNo", example = "")
	@JsonProperty("roomNo")
	private String roomNo;
	@ApiModelProperty(notes = "floorNo", example = "25")
	@JsonProperty("floorNo")
	private String floorNo;
	@ApiModelProperty(notes = "moo", example = "")
	@JsonProperty("moo")
	private String moo;
	@ApiModelProperty(notes = "soi", example = "")
	@JsonProperty("soi")
	private String soi;
	@ApiModelProperty(notes = "villageOrbuilding")
	@JsonProperty("villageOrbuilding")
	private String villageOrbuilding;
	@ApiModelProperty(notes = "street")
	@JsonProperty("street")
	private String street;
	@ApiModelProperty(notes = "zipcode", example = "10800")
	@JsonProperty("zipcode")
	private String zipcode;
	@ApiModelProperty(notes = "provinceNameTh")
	@JsonProperty("provinceNameTh")
	private String provinceNameTh;
	@ApiModelProperty(notes = "provinceNameEn", example = "Bangkok")
	@JsonProperty("provinceNameEn")
	private String provinceNameEn;
	@ApiModelProperty(notes = "provinceCode", example = "10800")
	@JsonProperty("provinceCode")
	private String provinceCode;
	@ApiModelProperty(notes = "districtNameTh")
	@JsonProperty("districtNameTh")
	private String districtNameTh;
	@ApiModelProperty(notes = "districtNameEn")
	@JsonProperty("districtNameEn")
	private String districtNameEn;
	@ApiModelProperty(notes = "subDistrictNameTh")
	@JsonProperty("subDistrictNameTh")
	private String subDistrictNameTh;
	@ApiModelProperty(notes = "subDistrictNameEn", example = "Bangsue")
	@JsonProperty("subDistrictNameEn")
	private String subDistrictNameEn;
	@ApiModelProperty(notes = "postcode", example = "10800")
	@JsonProperty("postcode")
	private String postcode;

}
