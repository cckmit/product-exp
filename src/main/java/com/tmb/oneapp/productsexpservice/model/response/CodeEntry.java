package com.tmb.oneapp.productsexpservice.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CodeEntry {

	@JsonProperty("activeStatus")
	private String activeStatus;
	@JsonProperty("categoryCode")
	private String categoryCode;
	@JsonProperty("entryCode")
	private String entryCode;
	@JsonProperty("entryID")
	private String entryID;
	@JsonProperty("entryName")
	private String entryName;
	@JsonProperty("entryName2")
	private String entryName2;
	@JsonProperty("entrySource")
	private String entrySource;
	@JsonProperty("extValue1")
	private String extValue1;
	@JsonProperty("extValue2")
	private String extValue2;
	@JsonProperty("groupId")
	private String groupId;
	@JsonProperty("refEntryCode")
	private String refEntryCode;

}
