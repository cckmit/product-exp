package com.tmb.oneapp.productsexpservice.model.productexperience.transaction.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionValidationRequest {

    private String orderType;

    private String switchFundCode;

    private String fundHouseCode;

    private String fundCode;

    private String portfolioNumber;

    private String orderAmount;

    private String redeemType;

    private String fullRedemption;

    private String fundLotRedeem;

    private String orderUnit;

    private String redeemReason;

    private String additionalRedeemReason;

    private String transferBank;

    private String transferBranch;

    private String bankAccountType;

    private String bankAccountNumber;

    private String orderDateTime;

    private String fundName;

    private String crmId;

    private String fundThaiClassName;

    private String fundEnglishClassName;

    @JsonProperty(value = "fundClassNameThHubSource")
    private String fundClassNameThaiSourceHub;

    @JsonProperty(value = "fundClassNameThHubTarget")
    private String fundClassNameThaiTargetHub;
}
