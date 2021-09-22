package com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class OrderCreationPaymentRequest {

    @NotBlank
    private String orderType;

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

    private String switchFundCode;

    @NotBlank
    private String orderDateTime;

    @NotBlank
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

    private Card card;

    private Fee fee;

    private Merchant merchant;

    /* additional fields */
    @NotBlank
    private String refId;

    @JsonAlias({"creditCard", "isCreditCard"})
    private boolean creditCard;

}
