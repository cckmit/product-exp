package com.tmb.oneapp.productsexpservice.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FundListBySuitScoreRequest {

    @NotNull
    private String suitScore;
}
