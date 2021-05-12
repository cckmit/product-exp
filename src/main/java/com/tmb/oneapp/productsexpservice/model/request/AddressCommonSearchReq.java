package com.tmb.oneapp.productsexpservice.model.request;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class AddressCommonSearchReq {

	@NotEmpty
	@JsonProperty("field")
	private String field;

	@NotEmpty
	@JsonProperty("search")
	private String search;

}
