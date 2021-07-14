package com.tmb.oneapp.productsexpservice.model.request.notification;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class FlexiLoanSubmissionWrapper {
    private String productCode;
    private String featureType;
    private String appRefNo;
    private String productName;
    private String customerName;
    private String idCardNo;
    private String finalLoanAmount;
    private BigDecimal tenor;
    private String interestRate;
    private String requestAmount;
    private String applyDate;
    private String paymentMethod;
    private String email;
    private String botAnswer1;
    private String botAnswer2;
    private String disburseAccountNo;
    private String consentDate;
    private String consentTime;
    private String ncbConsentFlag;
    private String dueDate;
    private String firstPaymentDueDate;
    private String nextPaymentDueDate;
    private String installment;
    private String cashDisbursement;
    private String currentLoan;
    private String currentAccount;
    private String interestRateTeir;
    private String rateTypeValue;
    private String showBOTFields;
    private String underwriting;

    private List<String> attachments;
}
