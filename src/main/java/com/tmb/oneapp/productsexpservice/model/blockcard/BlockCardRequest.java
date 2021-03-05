package com.tmb.oneapp.productsexpservice.model.blockcard;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BlockCardRequest {
	@ApiModelProperty(notes = "accountId", required = true, example = "0000000050078360018000167")
	@JsonProperty("account_id")
	private String accountId;
	@ApiModelProperty(notes = "blockReason", required = true, example = "L")
	@JsonProperty("block_reason")
	private String blockReason;
}
