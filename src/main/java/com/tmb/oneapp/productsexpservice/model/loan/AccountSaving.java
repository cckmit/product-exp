package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccountSaving {
    private String totalAvailableBalance;
    private List<DepositAccount> depositAccountLists;
    private List<ProductGroupFlag> productGroupFlag;
    private List<String> mutualFundAccounts;
    private List<LoanAccount> loanAccounts;
    private List<LoanAccount> hpAccounts;
}

