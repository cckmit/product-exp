package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class InstallmentPromotion {

    @JsonProperty("cashChillChillFlagDW")
    private String cashChillChillFlagDW;
    @JsonProperty("cashTransferFlagDW")
    private String cashTransferFlagDW;
    @JsonProperty("cashChillChillFlagAllow")
    private String cashChillChillFlagAllow;
    @JsonProperty("cashTransferFlagAllow")
    private String cashTransferFlagAllow;
    @JsonProperty("cutOfTier")
    private String cutOfTier;
    @JsonProperty("normalRate")
    private String normalRate;
    @JsonProperty("groupAccountId")
    private String groupAccountId;
    @JsonProperty("promoSegment")
    private String promoSegment;
    @JsonProperty("effectiveDate")
    private String effectiveDate;
    @JsonProperty("expiryDate")
    private String expiryDate;
}