package com.tmb.oneapp.productsexpservice.model.response.lending;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EAppResponse {
    String productNameTh;
    String productType;
    String appNo;
    String employmentStatus;
    String salary;
    BigDecimal otherIncome;
    BigDecimal creditLimit;
    BigDecimal tenor;
    BigDecimal interest;
    String loanReceiveAccount;
    String paymentPlan;
    String paymentMethod;
    String paymentAccountName;
    String paymentAccountNumber;
    String paymentCriteria;
    String botQ1;
    String botQ2;

    String idType;
    String idNumber;
    String issueCountry;
    Date issueDate;
    Date expiryDate;
    String nameTh;
    String nameEn;
    Date birthDay;
    String mobileNo;
    String highestEducation;
    String sourceOfIncome;
    String nationality;
    String maritalStatus;
    String placeOfBirth;
    String email;
    String contactAddress;
    String residentStatus;

    String rmOccupation;
    String occupation;
    String businessType;
    String businessSubType;
    String contractType;
    String workPeriodYear;
    String workPeriodMonth;
    String workName;
    String workAddress;
    String workTel;
    String workTelEx;
    String incomeBank;
    String incomeAccount;
    String cashFlow;
    String sharePercent;
    String incomeCountry;
    String eStatement;
    String delivery;
}
