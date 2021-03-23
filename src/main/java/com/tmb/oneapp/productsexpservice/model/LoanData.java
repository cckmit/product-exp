package com.tmb.oneapp.productsexpservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class LoanData {

    @JsonProperty("ProviderUserID")
    private String providerUserID;
    @JsonProperty("WSRecord")
    private List<LoanDetails> wSRecord;
}
