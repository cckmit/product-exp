package com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupationResponseBody {

    private String crmId;

    private String occupationCode;

    private String positionDescription;
}
