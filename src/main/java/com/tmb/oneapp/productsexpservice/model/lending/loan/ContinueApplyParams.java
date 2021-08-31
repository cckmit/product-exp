package com.tmb.oneapp.productsexpservice.model.lending.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ContinueApplyParams {
    private long caId;
    private String appRefNo;
    private String titlePage;
}
