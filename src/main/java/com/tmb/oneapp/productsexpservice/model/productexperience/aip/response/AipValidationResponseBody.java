package com.tmb.oneapp.productsexpservice.model.productexperience.aip.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AipValidationResponseBody {

    private String orderType;

    private String fundCode;

    private String portfolioNumber;

    private List<Warning> warningList;
}
