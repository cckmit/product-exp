package com.tmb.oneapp.productsexpservice.model.response.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanCustomerResponse {

    private List<LoanCustomerFeature> features;
    private List<LoanCustomerInstallment> installments;
    private List<LoanCustomerDisburstAccount> disburstAccounts;
    private List<Pricing> pricings;
    private AnnualInterest annualInterest;

}
