package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InterestRateResponse {
    private String productCode;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal interestRate;
    private BigDecimal rangeIncomeMin;
    private BigDecimal rangeIncomeMaz;
    private BigDecimal revenueMultiple;
    private String employmentStatusId;
    private String employmentStatus;
}
