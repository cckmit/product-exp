package com.tmb.oneapp.productsexpservice.model.loan.loanfacility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanFacilityPricing {
    private Long id;
    private Long ccId;
    private String facId;
    private Double installment;
    private String tier;
    private String rateType;
    private String percentSign;
    private Double calculatedRate;
    private Double rateVaraince;
    private Long yearFrom;
    private Long yearTo;
    private Long monthFrom;
    private Long monthTo;
    private String interestRate;
    private String pricingType;
}
