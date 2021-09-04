package com.tmb.oneapp.productsexpservice.model.lending.loan;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tmb.common.model.RslCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProductDetailResponse {
    private LoanType loanType;
    private String productCode;
    private String productNameTh;
    private String productNameEn;
    private boolean alreadyHasProduct;
    private boolean flexiOnly;
    private String contentLink;
    private ProductStatus status;
    private FlowType flowType;
    private ContinueApplyNextScreen continueApplyNextStep;
    private ContinueApplyParams continueApplyParams;
    private Object productData;
    private List<RslCode> defaultRslCode;
}