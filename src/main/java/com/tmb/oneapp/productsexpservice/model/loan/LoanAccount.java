package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanAccount {
    private String loanType;
    private String accountNumber;
    private String accountTypeDescEn;
    private String accountTypeDescTh;
    private String license;
    private String productCode;
    private String productIcon;
    private String productNameEn;
    private String productNameTh;
    private String productNickname;
    private String dueDate;
    private String dueAmount;
    private String paidAmount;
    private String outstandingAmount;
    private String paidPeriod;
    private String remainingPeriod;
    private List<AccountShortcut> shortcuts;
    private String productOrder;
}
