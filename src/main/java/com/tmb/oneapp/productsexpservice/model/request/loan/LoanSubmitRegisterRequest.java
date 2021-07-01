package com.tmb.oneapp.productsexpservice.model.request.loan;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanSubmitRegisterRequest {
    private String appType;
    private BigDecimal requestCreditLimit;
    private String productCode;
    private BigDecimal limitApplied;
    private BigDecimal tenure;
    private String mailPreference;
    private String cardDeliveryAddress;
    private String debitAccountName;
    private String debitAccountNo;
    private String disburstBankName;
    private String disburstAccountName;
    private String disburstAccountNo;
    private String paymentMethod;
    private String paymentAccountNo;
    private String paymentAccountName;
    private String mailingPreference;
    private String cardDelivery;
    private BigDecimal payMethodCriteria;
    private String loanWithOtherBank;
    private String considerLoanWithOtherBank;
    private String bankNo;
    private String cifRelationship;
    private String cifRelationshipCode;
    private String cifRelCode;
    private String creditCards;
    private String employmentStatus;
    private String incomeBasicSalary;
    private String inTotalIncome;
    private String incomeOtherIncome;
    private String incomeDeclared;
    private String employmentFinalTotalIncome;
    private String natureOfRequest;
    private String saleChannel;


}
