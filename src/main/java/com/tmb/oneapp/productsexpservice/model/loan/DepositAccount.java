package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DepositAccount {
    private String productCode;
    private String productNickname;
    private String productNameTh;
    private String productNameEn;
    private String accountNumber;
    private String accountStatus;
    private String accountType;
    private String accountName;
    private String branchCode;
    private String relationshipCode;
    private String allowFromForBillPayTopUpEpayment;
    private String allowTransferFromAccount;
    private String allowSetQuickBalance;
    private String allowPayLoanDirectDebit;
    private String allowReceiveLoanFund;
    private String waiveFeeForBillpay;
    private String waiveFeeForPromptPay;
    private String availableBalance;
    private String productIconUrl;
    private String productConfigSortOrder;
    private List<AccountShortcut> shortcuts;

}
