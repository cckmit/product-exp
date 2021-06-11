package com.tmb.oneapp.productsexpservice.model.fundallocation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundClassList;
import com.tmb.oneapp.productsexpservice.model.response.fund.fundallocation.FundAllocationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@NotNull
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuggestAllocationBodyResponse {
    private FundClassList fundClassList;
    private FundAllocationResponse fundAllocation;
}
