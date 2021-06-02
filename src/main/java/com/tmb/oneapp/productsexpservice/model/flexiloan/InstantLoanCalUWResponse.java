package com.tmb.oneapp.productsexpservice.model.flexiloan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerPricing;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InstantLoanCalUWResponse {
    private String status;
    private String product;
    private BigDecimal topUpAmount;
    private BigDecimal loanAmount;
    private BigDecimal installmentAmount;
    private BigDecimal tenor;
    private BigDecimal interestRate;
    private String PayDate;
    private String disburstAccountNo;
    private BigDecimal creditLimit;
    private List<LoanCustomerPricing> pricings;

    private Calendar loanContractDate;
    private String firstPaymentDueDate;

}
