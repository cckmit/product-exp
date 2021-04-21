package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class LoanStatementResponse {

    @JsonProperty("status")
    private Status status;
    @JsonProperty("additional_status")
    private List<AdditionalStatus> additionalStatus;
    @JsonProperty("account")
    private AccountResponse response;
}
