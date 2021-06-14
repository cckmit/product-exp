package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanSubmissionResponse {
    private String statusWorking;
    private BigDecimal monthlyIncome;
    private String accountNo;
    private String accountName;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal tenure;
    private BigDecimal requestAmount;
    private BigDecimal interestRate;
    private BigDecimal payAmount;

}
