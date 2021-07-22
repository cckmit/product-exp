package com.tmb.oneapp.productsexpservice.model.productexperience.aip.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warning {

    private String code;

    private String description;
}
