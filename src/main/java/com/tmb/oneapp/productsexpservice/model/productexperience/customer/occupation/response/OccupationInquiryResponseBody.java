package com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupationInquiryResponseBody {

    private String crmId;

    private String occupationCode;

    private String occupationDescription;

    private String positionDescription;

    private String requirePosition;

    private String requireUpdate;
}
