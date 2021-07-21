package com.tmb.oneapp.productsexpservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PtesDetail {
    private String portfolioNumber;
    private String portfolioFlag;
    private String ownershipType;
    private String ownershipDesc;
    private String portfolioValue;
}
