package com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class OrderCreationPaymentRequestBody {

    @NotBlank
    private String orderType;

    private String switchFundCode;

    @NotBlank
    private String fundHouseCode;

    @NotBlank
    private String fundCode;

    @NotBlank
    private String portfolioNumber;

    @NotBlank
    private String redeemType;

    private String fullRedemption;

    private String orderAmount;

    private String orderUnit;

    private String fundLotRedeem;

    private String redeemReason;

    private String additionalRedeemReason;

    @NotBlank
    private String transferBank;

    @NotBlank
    private String transferBranch;

    @NotBlank
    private String bankAccountType;

    private String bankAccountNumber;

    @NotBlank
    private String orderDateTime;

    private String appId;

    @NotBlank
    private String paymentChannel;

    @NotBlank
    private String paymentId;

    private String epayCode;

    @Valid
    @NotBlank
    private Account fromAccount;

    @Valid
    @NotBlank
    private Account toAccount;

    private Fee fee;

    /* additional fields */
    @NotBlank
    private String refId;

    @JsonAlias({"creditCard", "isCreditCard"})
    private boolean creditCard;

    private String fundName;

    private String fundThaiClassName;

    private String fundEnglishClassName;

    private String sourceFundClassName;

    private String targetFundClassName;

    // use internal
    @JsonIgnore
    private Card card;

    @JsonIgnore
    private Merchant merchant;

}
