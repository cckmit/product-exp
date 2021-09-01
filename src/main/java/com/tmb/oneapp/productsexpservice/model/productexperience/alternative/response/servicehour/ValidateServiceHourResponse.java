package com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour;

import com.tmb.common.model.TmbStatus;
import lombok.Data;

@Data
public class ValidateServiceHourResponse {

    private String code;

    private String message;

    private String service;

    private String description;

    private String startTime;

    private String endTime;

}
