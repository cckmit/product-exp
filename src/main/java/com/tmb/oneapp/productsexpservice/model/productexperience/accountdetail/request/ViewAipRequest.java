package com.tmb.oneapp.productsexpservice.model.productexperience.accountdetail.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class ViewAipRequest {

    private String crmId;

    private String getFlag;

    private String portfolioList;

    private String fundCode;
}
