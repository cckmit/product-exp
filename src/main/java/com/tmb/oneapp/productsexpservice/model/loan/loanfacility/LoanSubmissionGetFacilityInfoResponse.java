package com.tmb.oneapp.productsexpservice.model.loan.loanfacility;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class LoanSubmissionGetFacilityInfoResponse {

    private Long id;
    private Long caId;
    private String mrtaFlag;
    private BigDecimal mrtaAmount;
    private String mrtaIncludeInLoanAmountFlag;
    private BigDecimal mrtaSumInsured;
    private BigDecimal mrtaYrsCoverage;
    private String hostProductCode;
    private String hostProjectCode;
    private String facilityCode;
    private String productCode;
    private BigDecimal amountFinance;
    private BigDecimal totalCreditLimit;
    private BigDecimal limitApplied;
    private Long tenure;
    private String financialInstitution;
    private BigDecimal osLimit;
    private BigDecimal monthlyInstallment;
    private String existingAccountNo;
    private String accountName;
    private String suffix;
    private BigDecimal existingCreditLimit;
    private BigDecimal existingOsBalance;
    private BigDecimal totalRepaymentAmount;
    private BigDecimal existMaxCreditLimit;
    private String facilityStatus;
    private String hostAaNo;
    private String facilityPurchaseCode;
    private String facilityPurchaseDesc;
    private String caCampaignCode;
    private String customerSegment;
    private String mailingPreference;
    private BigDecimal existLimit;
    private String paymentDueDate;
    private Date contractDate;
    private String rePayMent;
    private String cardDelivery;
    private String payMethodCriteria;
    private String mofDocumentCode;
    private String mofLoanID;
    private BigDecimal creditLimitFromMof;
    private String existLoan;
    private String sameGroup;
    private BigDecimal outStandingBalance;
    private BigDecimal monthlyInstall;
    private String financialInstitutionName;
    private Date rfContractDate;
    private String facilitySavedFlag;
    private String disburstBankName;
    private String disburstAccountName;
    private String disburstAccountNo;
    private String paymentMethod;
    private String paymentAccountName;
    private String paymentAccountNo;
    private String loanWithOtherBank;
    private String considerLoanWithOtherBank;
    private String hostAcfNo;
    private String hostAccountName;
    private String hostAccountNoSuffix;
    private BigDecimal existingTenure;
    private String firstPaymentDueDate;
    private String featureType;
    private String instantRequestLead;
    private LoanFacilityFeature feature;
    private List<LoanFacilityPricing> pricings;

}
