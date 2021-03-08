package com.tmb.oneapp.productsexpservice.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CustomerFirstUsage implements Serializable {
    private String crmId;
    private String deviceId;
    private String serviceTypeId;
    private String timestamp;

}