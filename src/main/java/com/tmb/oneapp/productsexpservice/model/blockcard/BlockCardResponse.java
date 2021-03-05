package com.tmb.oneapp.productsexpservice.model.blockcard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockCardResponse {

	@JsonProperty("status")
	private Status status;
}
