package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "status_code" })
@Data
public class StatusResponse {

	@JsonProperty("status_code")
	private String statusCode;
	@JsonProperty("error_status")
	private List<ErrorStatus> errorStatus;
}
