package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CardCashAdvance {
	private BigDecimal cashAdvFeeRate;
	private BigDecimal cashAdvFeeFixedAmt;
	private String cashAdvFeeModel;
	@JsonProperty("cash_adv_fee_VAT_rate")
	private BigDecimal cashAdvFeeVATRate;
	private BigDecimal cashAdvIntRate;
}
