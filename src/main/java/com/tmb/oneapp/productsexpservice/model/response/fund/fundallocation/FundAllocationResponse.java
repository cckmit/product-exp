package com.tmb.oneapp.productsexpservice.model.response.fund.fundallocation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundAllocationResponse {
    private String suitabilityScore;
    private String modelNumber;
    @JsonProperty("fundClassList")
    private List<FundSuggestAllocationList> fundSuggestAllocationList;
}
