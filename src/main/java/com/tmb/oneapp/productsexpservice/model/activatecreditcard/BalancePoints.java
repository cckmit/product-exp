package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BalancePoints {
	private BigDecimal pointEarned;
	private BigDecimal pointUsed;
	private BigDecimal pointAvailable;
	private BigDecimal pointRemain;
	private BigDecimal expiryPoints;
	private String expiryDate;
}
