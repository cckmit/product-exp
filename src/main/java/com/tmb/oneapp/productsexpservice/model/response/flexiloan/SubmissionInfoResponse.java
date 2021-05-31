package com.tmb.oneapp.productsexpservice.model.response.flexiloan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.response.loan.Pricing;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionInfoResponse {
    private BigDecimal topUpAmount;
    private BigDecimal loanAmount;
    private BigDecimal tenor;
    private BigDecimal interestRate;
    private String PayDate;
    private String disburstAccountNo;
    private BigDecimal creditLimit;
    private Pricing[] pricings;
}
