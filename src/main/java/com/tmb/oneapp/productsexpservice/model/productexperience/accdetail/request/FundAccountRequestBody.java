package com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundAccountRequestBody {

    private String portfolioNumber;

    private String fundCode;
}
