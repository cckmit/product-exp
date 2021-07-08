package com.tmb.oneapp.productsexpservice.model.response.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProductData {
    private String rslCode;
    private String contentLink;
    private String productNameEn;
    private String productNameTh;
    private String productDescEn;
    private String productDescTh;
    private String iconId;
}
