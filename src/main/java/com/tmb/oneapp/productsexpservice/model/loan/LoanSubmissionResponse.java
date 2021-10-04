package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerDisburstAccount;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanSubmissionResponse {
    private BigDecimal tenure;
    private BigDecimal payAmount;
    private List<LoanCustomerDisburstAccount> receiveAccounts;
    private List<LoanCustomerDisburstAccount> paymentAccounts;
    private List<InterestRate> interestRateList;
    private List<RangeIncome> rangeIncomeList;
    private boolean allowApplySoSmart;

}
