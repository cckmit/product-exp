package com.tmb.oneapp.productsexpservice.model.setpin;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SetPinReqParameter {
	@ApiModelProperty(notes = "accountId", required = true, example = "0000000050078360018000167")
	@JsonProperty("account_id")
	private String accountId;
	@ApiModelProperty(notes = "e2eesid", required = true, example = "0001MLodtB7S11cavuDghRUsTdS5lX_1yCst1UjKsgKEsgsSswtzyUFsQdyXNT7ZU9zEOsxBCuwxc")
	@JsonProperty("e2eesid")
	private String e2eesid;
	@ApiModelProperty(notes = "rpin", required = true, example = "0001MLodtB7S11cavuDghRUsTdS5lX_1yCst1UjKsgKEsgsSswtzyUFsQdyXNT7ZU9zEOsxBCuwxc")
	@JsonProperty("rpin")
	private String rpin;
	@ApiModelProperty(notes = "anb", required = true, example = "0630000000092")
	@JsonProperty("anb")
	private String anb;
}
