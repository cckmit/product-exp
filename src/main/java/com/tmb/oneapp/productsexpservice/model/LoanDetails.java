package com.tmb.oneapp.productsexpservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class LoanDetails {

    @JsonProperty("ProductType")
    private String productType;
    @JsonProperty("AppNo")
    private String appNo;
    @JsonProperty("CarBrand")
    private String carBrand;
    @JsonProperty("CarFamily")
    private String carFamily;
    @JsonProperty("CarRegisNo")
    private String carRegisNo;
    @JsonProperty("HPAPStatus")
    private String hPAPStatus;
    @JsonProperty("StatusDate")
    private String statusDate;
    @JsonProperty("Msg")
    private String msg;
    @JsonProperty("HPAccountNo")
    private String hPAccountNo;
    @JsonProperty("StatusCode")
    private String statusCode;
}
