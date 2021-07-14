package com.tmb.oneapp.productsexpservice.model.productexperience.accountdetail.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    private String aipCode;

    private String transType;

    private String portfolioNumber;

    private String nickname;

    private String fundCode;

    private String fundShortName;

    private String fundEnglishName;

    private String fundThaiName;

    private String frequency;

    private String frequencyDate;

    private String frequencyDay;

    private String investAmount;

    private String investUnit;

    private String planStartDate;

    private String planEndDate;

    private String accountType;

    private String accountName;

    private String accountNumber;

    private String makerBranchCode;

    private String riskRate;

    private String riskScore;
}
