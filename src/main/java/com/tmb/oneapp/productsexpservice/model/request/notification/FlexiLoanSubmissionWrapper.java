package com.tmb.oneapp.productsexpservice.model.request.notification;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class FlexiLoanSubmissionWrapper {
    private String appRefNo;
    private String productName;
    private String customerName;
    private String citizenId;
    private BigDecimal creditLimit;
    private String productCode;
    private BigDecimal interestRateDS;
    private BigDecimal interestRate;
    private String rateTypeValue;
    private String disburseAccountNo;
    private String featureType;
    private BigDecimal outstandingBal;
    private BigDecimal existingOsBal;
    private String existingAcctNo;
    private BigDecimal tenor;
    private String applyDate;
    private String firstPaymentDueDate;
    private String nextPaymentDueDate;
    private BigDecimal installmentAmount;
    private String paymentMethod;
    private String eStatement;
    private String showBOTFields;
    private String loanWithOtherBank;
    private String considerLoanWithOtherBank;
    private String tenureFeat;
    private BigDecimal requestAmount;
    private Boolean isReject;
    private String ncbConsentFlag;
    private String ConsentDate;
    private String ConsentTime;
    private String email;
    private List<String> attachments;
}
