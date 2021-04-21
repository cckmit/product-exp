package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class LoanStatementRequest {

    @ApiModelProperty(notes = "account_id", required=true, example="00015719933001")
    @JsonProperty("account_id")
    private String accountId;
    @ApiModelProperty(notes = "start_date", required=true, example="2020-03-01")
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    @ApiModelProperty(notes = "end_date", required=true, example="2021-03-25")
    private String endDate;
}