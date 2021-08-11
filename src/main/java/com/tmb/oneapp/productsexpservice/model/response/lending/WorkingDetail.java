package com.tmb.oneapp.productsexpservice.model.response.lending;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tmb.oneapp.productsexpservice.model.personaldetail.Address;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WorkingDetail {
    private String employmentStatus;
    private String rmOccupation;
    private String occupation;
    private String contractEmployedFlag;
    private String businessType;
    private String businessSubType;
    private String employmentName;
    private Address address;
    private String tel;
    private String exTel;
    private BigDecimal incomeBasicSalary;
    private BigDecimal incomeOtherIncome;
    private String incomeBank;
    private String incomeBankAccountNumber;
    private BigDecimal incomeDeclared;
    private BigDecimal incometotalLastMthCreditAcct1;
    private BigDecimal incomeSharedHolderPercent;
    private String incomeType;
    private String sciCountry;
    private String cardDelivery;
    private String emailStatementFlag;
}
