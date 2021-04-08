package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter

public class EligibleLeadRequest {
    @ApiModelProperty(notes = "groupAccountId", required=true, example="0000000050080760015")
    @JsonProperty("groupAccountId")
    private String groupAccountId;
    @ApiModelProperty(notes = "disbursementDate", required=true, example="2021-02-01")
    @JsonProperty("disbursementDate")
    private String disbursementDate;
}
