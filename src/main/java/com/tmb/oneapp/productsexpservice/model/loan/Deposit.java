package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Deposit {

    @ApiModelProperty(notes = "from_account_id", required = true, example = "0000000050078010407010291")
    @JsonProperty("from_account_id")
    private String fromAccountId;
    @ApiModelProperty(notes = "from_account_type", required = true, example = "CCA")
    @JsonProperty("from_account_type")
    private String fromAccountType;
    @ApiModelProperty(notes = "to_account_id", required = true, example = "00000032323891")
    @JsonProperty("to_account_id")
    private String toAccountId;
    @ApiModelProperty(notes = "to_account_type", required = true, example = "SDA")
    @JsonProperty("to_account_type")
    private String toAccountType;
    @ApiModelProperty(notes = "amounts", required = true, example = "5000.00")
    @JsonProperty("amounts")
    private String amounts;
    @ApiModelProperty(notes = "transferred_date", required = true, example = "2021-03-11")
    @JsonProperty("transferred_date")
    private String transferredDate;
    @ApiModelProperty(notes = "waiver_code", required = true, example = "F")
    @JsonProperty("waiver_code")
    private String waiverCode;
    @ApiModelProperty(notes = "expired_date", required = true, example = "2511")
    @JsonProperty("expired_date")
    private String expiredDate;
    @ApiModelProperty(notes = "reference_code", required = true, example = "NB2016710001973200")
    @JsonProperty("reference_code")
    private String referenceCode;
    @ApiModelProperty(notes = "model_type", required = true, example = "CA")
    @JsonProperty("model_type")
    private String modelType;
    @ApiModelProperty(notes = "order_no", required = true, example = "")
    @JsonProperty("order_no")
    private String orderNo;
}