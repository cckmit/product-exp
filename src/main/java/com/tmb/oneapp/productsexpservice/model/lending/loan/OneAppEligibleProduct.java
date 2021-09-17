package com.tmb.oneapp.productsexpservice.model.lending.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OneAppEligibleProduct {
    private String productCategory;
    private String productCode;
    private String productType;
    private String campaignCode;
    private String facilityCode;
    private String productNameEn;
    private String productNameTh;
    private String interestRate;
    private List<PaymentCriteriaOption> paymentCriteriaOptions;
    private String loanReqMax;
    private String loanReqMin;
    private String osLimit;
    private String sourceOfData;
}
