package com.tmb.oneapp.productsexpservice.model.loan.loanfacility;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanFacilityFeature {
    private String id;
    private Double requestPercent;
    private BigDecimal requestAmount;
    private Long tenure;
    private String disbAcctName;
    private String disbAcctNo;
    private String disbBankCode;
}
