package com.tmb.oneapp.productsexpservice.model.request.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerDisburstAccount;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateApplicationRequest {
    private BigDecimal incomeBasicSalary;
    private BigDecimal incomeDeclared;
    private BigDecimal incomeOtherIncome;
    private String employmentStatus;
    private LoanCustomerDisburstAccount receiveAccount;
    private LoanCustomerDisburstAccount paymentAccount;
}
