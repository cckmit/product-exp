package com.tmb.oneapp.productsexpservice.model.response.lending;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.oneapp.productsexpservice.model.response.DependDefaultEntry;

import lombok.Data;

@Data
public class WorkProfileInfoResponse {

	@JsonProperty("workstatus")
	private DependDefaultEntry workstatus;
	@JsonProperty("occupation")
	private DependDefaultEntry occupation;
	@JsonProperty("businessType")
	private DependDefaultEntry businessType;
	@JsonProperty("subBusinessType")
	private DependDefaultEntry subBusinessType;
	@JsonProperty("sourceIncomes")
	private DependDefaultEntry sourceIncomes;
	@JsonProperty("countryIncomes")
	private DependDefaultEntry countryIncomes;

}
