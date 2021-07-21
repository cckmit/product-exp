package com.tmb.oneapp.productsexpservice.model.productexperience.aip.response;

import com.tmb.oneapp.productsexpservice.model.response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AipValidationResponse {

    private Status status;

    private AipValidationResponseBody data;
}
