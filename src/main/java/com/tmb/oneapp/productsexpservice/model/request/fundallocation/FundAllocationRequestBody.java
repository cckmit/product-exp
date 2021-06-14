package com.tmb.oneapp.productsexpservice.model.request.fundallocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundAllocationRequestBody {
    private String suitabilityScore;
}
