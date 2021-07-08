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
    private String monthlyIncome;
    private String accountNo;
    private String accountName;
    private int minAmount;
    private int maxAmount;
    private BigDecimal tenure;
    private int requestAmount;
    private int interestRate;
    private BigDecimal payAmount;

}
