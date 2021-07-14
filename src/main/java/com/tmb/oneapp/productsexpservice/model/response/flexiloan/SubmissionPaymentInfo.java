package com.tmb.oneapp.productsexpservice.model.response.flexiloan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Calendar;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SubmissionPaymentInfo {
    private String paymentMethod;
    private String featureType;
    private String eStatement;
    private String otherBank;
    private String otherBankInProgress;
    private BigDecimal outStandingBalance;
    private BigDecimal requestAmount;
    private BigDecimal installmentAmount;
    private BigDecimal tenure;
    private BigDecimal interestRate;
    private String payDate;
    private String disburstAccountNo;
    private BigDecimal creditLimit;
    private Calendar loanContractDate;
    private String firstPaymentDueDate;
    private String nextPaymentDueDate;
    private String rateType;
    private BigDecimal rateTypePercent;
    private String underwriting;
}

