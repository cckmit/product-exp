package com.tmb.oneapp.productsexpservice.model.productexperience.transaction.orderaip.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAIPRequestBody {

    @NotNull
    private String orderType;

    @NotNull
    private String fundHouseCode;

    @NotNull
    private String fundCode;

    @NotNull
    private String portfolioNumber;

    @NotNull
    private BigDecimal orderAmount;

    @NotNull
    private String orderDateTime;

    private String transferBank;

    private String transferBranch;

    @NotNull
    private String bankAccountType;

    @NotNull
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
