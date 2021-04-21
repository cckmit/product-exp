package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class FetchCreditCardDetailsReq {
	@JsonProperty("account_id")
	@ApiModelProperty(notes = "account_id", required=true, example="0000000050078360018000167")
	private String accountId;
}
