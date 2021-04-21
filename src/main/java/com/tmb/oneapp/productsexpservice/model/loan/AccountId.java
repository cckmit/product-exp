package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccountId {
    @ApiModelProperty(notes = "account_id", required=true, example="00016109738001")
    @JsonProperty("account_id")
    private String accountNo;
}
