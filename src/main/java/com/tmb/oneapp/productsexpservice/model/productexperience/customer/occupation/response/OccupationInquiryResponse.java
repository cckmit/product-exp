package com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response;

import com.tmb.oneapp.productsexpservice.model.response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupationInquiryResponse {

    private Status status;

    private OccupationInquiryResponseBody data;
}
