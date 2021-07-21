package com.tmb.oneapp.productsexpservice.model.productexperience.dca.validation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DcaValidationRequest {

    /* AIP AND TRANSACTION */
    @Builder.Default
    private String orderType = "P";

    @Builder.Default
    private String transferBank = "011";

    @Builder.Default
    private String transferBranch = "988";

    @NotNull
    private String fundHouseCode;

    @NotNull
    private String fundCode;

    @NotNull
    private String portfolioNumber;

    @NotNull
    private BigDecimal orderAmount;

    @NotNull
    @Builder.Default
    private String orderDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new Date());

    @NotNull
    private String bankAccountType;

    @NotNull
    private String bankAccountNumber;

    /* AIP */
    @Builder.Default
    private String frequency = "monthly";

    @Builder.Default
    private String weekDay = "";

    @Builder.Default
    private String quarterSchedule = "";

    @Builder.Default
    private String paymentReference = "";

    private String bankAccountId;

    private String aipStartDate;

    private String aipEndDate;

    private String dayOfMonth;

    private String investmentDate;

    private String creditCardExpiry;

    /* Transaction */
    @Builder.Default
    private String switchFundCode = "";

    @Builder.Default
    private String redeemType = null;

    @Builder.Default
    private String fullRedemption = null;

    @Builder.Default
    private String fundLotRedeem = "";

    @Builder.Default
    private String orderUnit = "";

    @Builder.Default
    private String redeemReason = "";

    @Builder.Default
    private String additionalRedeemReason = "";

    private String fundName;

    private String fundThaiClassName;

    private String fundEnglishClassName;

    @JsonProperty(value = "fundClassNameThHubSource")
    private String fundClassNameThaiSourceHub;

    @JsonProperty(value = "fundClassNameThHubTarget")
    private String fundClassNameThaiTargetHub;
}
