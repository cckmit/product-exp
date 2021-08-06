package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DirectDebit {

    @JsonProperty("account_id")
    private String accountId;
    @JsonProperty("sequence_no")
    private String sequenceNo;
    @JsonProperty("affiliate_sequence_no")
    private String affiliateSequenceNo;

}
