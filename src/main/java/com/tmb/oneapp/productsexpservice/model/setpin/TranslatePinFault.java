package com.tmb.oneapp.productsexpservice.model.setpin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslatePinFault {
	@JsonProperty("code")
	private Integer code;
	@JsonProperty("code/h")
	private String codeH;
	@JsonProperty("message")
	private String message;
	@JsonProperty("params")
	private Params params;
}
