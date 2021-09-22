package com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request;

import com.tmb.oneapp.productsexpservice.model.productexperience.mutualfund.HeaderRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellAndSwitchRequestBody {

    @NotBlank
    private String orderType;

    private String switchFundCode;

    @NotBlank
    private String fundHouseCode;

    @NotBlank
    private String fundCode;

    @NotBlank
    private String portfolioNumber;

    private String orderAmount;

    @NotBlank
    private String redeemType;

    private String fullRedemption;

    private String fundLotRedeem;

    private String orderUnit;

    private String redeemReason;

    private String additionalRedeemReason;

    @NotBlank
    private String transferBank;

    @NotBlank
    private String transferBranch;

    private String bankAccountType;

    private String bankAccountNumber;

    @NotBlank
    private String orderDateTime;

    private String fundName;

    private String crmId;

    private String fundThaiClassName;

    private String fundEnglishClassName;

    private String sourceFundClassName;

    private String targetFundClassName;

}
