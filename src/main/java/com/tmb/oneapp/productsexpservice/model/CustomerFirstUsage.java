package com.tmb.oneapp.productsexpservice.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class CustomerFirstUsage implements Serializable {
    private String crmId;
    private String deviceId;
    private String serviceTypeId;
    private String timestamp;

}