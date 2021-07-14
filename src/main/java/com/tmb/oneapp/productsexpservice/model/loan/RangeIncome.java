package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RangeIncome {
    private String productCode;
    private String productNameTh;
    private String productNameEng;
    private String maxLimit;
    private int minAmount;
    private int maxAmount;
    private String statusWorking;
    private long revenueMultiple;
}
