package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerDisburstAccount;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanCalculatorResponse {
    private BigDecimal incomeBasicSalary;
    private BigDecimal incomeDeclared;
    private BigDecimal incomeOtherIncome;
    private Boolean isWaiveDoc;
    private String employmentStatus;
    private LoanCustomerDisburstAccount receiveAccount;
    private LoanCustomerDisburstAccount paymentAccount;
}
