package com.tmb.oneapp.productsexpservice.model.blockcard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Status {
	@JsonProperty("status_code")
	private String statusCode;
	@JsonProperty("date")
	private String date;
	@JsonProperty("txn_id")
	private String txnId;

}
