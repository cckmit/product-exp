package com.tmb.oneapp.productsexpservice.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllowCashDayOne {
	@JsonProperty("allowCashDayOne")
    private boolean cashDayOne;
}
