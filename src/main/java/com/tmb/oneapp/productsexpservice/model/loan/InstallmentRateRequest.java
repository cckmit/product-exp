package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstallmentRateRequest {
    @ApiModelProperty(notes = "groupAccountId", required=true, example="0000000050080760015")
    @JsonProperty("groupAccountId")
    private String groupAccountId;
    @ApiModelProperty(notes = "amount", required=true, example="10000.00")
    @JsonProperty("amount")
    private String amount;
    @ApiModelProperty(notes = "billCycleCutDate", required=true, example="3")
    @JsonProperty("billCycleCutDate")
    private String billCycleCutDate;
    @ApiModelProperty(notes = "disbursementDate", required=true, example="2020-10-16")
    @JsonProperty("disbursementDate")
    private String disbursementDate;
    @ApiModelProperty(notes = "promoSegment", required=true, example="CS7")
    @JsonProperty("promoSegment")
    private String promoSegment;
    @ApiModelProperty(notes = "cashChillChillFlag", required=true, example="Y")
    @JsonProperty("cashChillChillFlag")
    private String cashChillChillFlag;
    @ApiModelProperty(notes = "cashTransferFlag", required=true, example="Y")
    @JsonProperty("cashTransferFlag")
    private String cashTransferFlag;
    @ApiModelProperty(notes = "getAllDetailFlag", required=true, example="N")
    @JsonProperty("getAllDetailFlag")
    private String getAllDetailFlag;
}