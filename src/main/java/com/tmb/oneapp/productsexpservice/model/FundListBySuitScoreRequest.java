package com.tmb.oneapp.productsexpservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


import javax.validation.constraints.NotNull;
@Data
public class FundListBySuitScoreRequest {
@NotNull
//@JsonProperty(value = "riskRate")
    private String suitScore;
}
