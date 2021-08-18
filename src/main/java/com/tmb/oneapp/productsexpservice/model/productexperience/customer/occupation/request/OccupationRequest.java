package com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupationRequest {

    private String occupationCode;

    private String positionDescription;
}
