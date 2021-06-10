package com.tmb.oneapp.productsexpservice.model.fundallocation.request;

import lombok.Data;

@Data

public class SuggestAllocationBodyRequest {
    private String crmId;
    private String suitabilityScore;
}
