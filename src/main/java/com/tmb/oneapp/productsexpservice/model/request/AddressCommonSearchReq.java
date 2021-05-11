package com.tmb.oneapp.productsexpservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressCommonSearchReq {

	@JsonProperty("field")
	private String field;

	@JsonProperty("search")
	private String search;

}
