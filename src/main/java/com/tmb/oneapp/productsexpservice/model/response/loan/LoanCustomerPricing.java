package com.tmb.oneapp.productsexpservice.model.response.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanCustomerPricing {
    private BigDecimal monthFrom;
    private BigDecimal monthTo;
    private BigDecimal rateVariance;
}