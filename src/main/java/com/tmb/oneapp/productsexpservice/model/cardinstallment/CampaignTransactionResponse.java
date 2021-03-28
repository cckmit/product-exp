package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CampaignTransactionResponse {

    @JsonProperty("status")
    private Status status;
    @JsonProperty("card_statement")
    private CardStatementReponse cardStatement;
    @JsonProperty("total_records")
    private Integer totalRecords;
    @JsonProperty("max_records")
    private Integer maxRecords;
    @JsonProperty("more_records")
    private String moreRecords;
    @JsonProperty("search_keys")
    private String searchKeys;

}
