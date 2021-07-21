package com.tmb.oneapp.productsexpservice.model.productexperience.aip.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AipValidationRequest {

    private String orderType;

    private String fundHouseCode;

    private String fundCode;

    private String portfolioNumber;

    private BigDecimal orderAmount;

    private String orderDateTime;

    private String transferBank;

    private String transferBranch;

    private String bankAccountType;

    private String bankAccountNumber;

    private String bankAccountId;

    private String aipStartDate;

    private String aipEndDate;

    private String frequency;

    private String weekDay;

    private String dayOfMonth;

    private String quarterSchedule;

    private String investmentDate;

    private String creditCardExpiry;

    private String paymentReference;
}
