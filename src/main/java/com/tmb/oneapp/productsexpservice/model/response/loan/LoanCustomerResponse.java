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
    private List<LoanCustomerTenure> installments;
    private List<LoanCustomerDisburstAccount> receiveAccounts;
    private List<LoanCustomerDisburstAccount> paymentAccounts;
    private List<LoanCustomerPricing> pricings;
    private AnnualInterest annualInterest;
}
