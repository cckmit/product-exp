package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

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
public class TemporaryCreditLimit {
	private Long amounts;
	private String effectiveDate;
	private String expiryDate;
	private String requestReason;
	private Long previousCreditLimit;
	private Long maxTempAllowance;
	private Long maxTempIncrease;
}
