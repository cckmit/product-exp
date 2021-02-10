package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockCodeResponse {
	@JsonProperty("block_code")
	String blockCode;
}
