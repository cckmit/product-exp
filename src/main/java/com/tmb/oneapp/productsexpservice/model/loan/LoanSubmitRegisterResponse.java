package com.tmb.oneapp.productsexpservice.model.loan;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanSubmitRegisterResponse {
    private String featureType;
    private BigDecimal approveAmount;
    private String disburstAccountNo;
    private String disburstAccountName;
    private String statusWorking;
    private String summary;
    private String bonus;
    private BigDecimal requestAmount;
    private String monthlyPayment;
    private String tenure;

}
