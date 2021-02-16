package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class GetCreditCardDetailsReq {
	@JsonProperty("account_id")
	private String accountId;
}
