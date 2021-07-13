package com.tmb.oneapp.productsexpservice.model.request.loan;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Calendar;

@Getter
@Setter
public class LoanSubmitRegisterRequest {

    private String paymentMethod;
    private String branchCode;
    private String bookBranchCode;
    private String authenCode;
    private String saleChannel;
    private String ncbConsentFlag;
    private String natureOfRequest;


    private String appType;
    private String cardInd;
    private String productType;
    private String cardBrand;
    private String campaignCode;
    private BigDecimal requestCreditLimit;
    private String debitAccountName;
    private String debitAccountNo;
    private String mailPreference;

    private String facilityCode;
    private String productCode;
    private String caCampaignCode;
    private BigDecimal limitApplied;
    private BigDecimal monthlyInstallment;
    private BigDecimal tenure;
    private BigDecimal interestRate;
    private String paymentDueDate;
    private String firstPaymentDueDate;
    private String loanWithOtherBank;
    private String considerLoanWithOtherBank;
    private String disburstBankName;
    private String disburstAccountName;
    private String disburstAccountNo;
    private String paymentAccountName;
    private String paymentAccountNo;
    private String mailingPreference;
    private String cardDelivery;


    private BigDecimal payMethodCriteria;
    private String bankNo;
    private String cifRelationship;
    private String cifRelationshipCode;
    private String cifRelCode;
    private String idType1;
    private String idNo1;
    private String hostCifNo;
    private String thaiName;
    private String thaiSurName;
    private String mobileNo;
    private Calendar birthDate;
    private String nationality;
    private String incomeType;
    private String creditCards;
    private String employmentStatus;
    private String incomeBasicSalary;
    private String inTotalIncome;
    private String incomeOtherIncome;
    private String incomeDeclared;
    private String employmentFinalTotalIncome;


}
