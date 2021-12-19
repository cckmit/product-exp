package com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.response;

import com.tmb.oneapp.productsexpservice.model.response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FatcaResponse {

    private Status status;

    private FatcaResponseBody data;
}
