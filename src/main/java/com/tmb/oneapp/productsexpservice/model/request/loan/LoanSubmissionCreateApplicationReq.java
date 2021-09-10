package com.tmb.oneapp.productsexpservice.model.request.loan;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanSubmissionCreateApplicationReq {
    private Long caId;
    private String productCode;
    private String employmentStatus;
    private BigDecimal incomeBasicSalary;
    private BigDecimal inTotalIncome;
    private BigDecimal incomeDeclared;
    private BigDecimal incomeOtherIncome;
    private String paymentMethod;
    private String debitAccountNo;
    private String debitAccountName;
    private String paymentCriteria;
    private BigDecimal limitApplied;
    private BigDecimal tenure;
    private String disburstBankName;
    private String disburstAccountNo;
    private String disburstAccountName;
    private String paymentAccountNo;
    private String paymentAccountName;
    private String payMethodCriteria;
    private String loanWithOtherBank;
    private String considerLoanWithOtherBank;
    private BigDecimal employmentFinalTotalIncome;
    private String campaignCode;
    private String cardBrand;
    private String cardInd;
    private String productType;
}
