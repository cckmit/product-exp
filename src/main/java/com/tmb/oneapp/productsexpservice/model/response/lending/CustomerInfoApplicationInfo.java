package com.tmb.oneapp.productsexpservice.model.response.lending;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CustomerInfoApplicationInfo {
    
	private String thaiName;
    private String thaiSurName;
    private String citizenIdOrPassportNo;
    private String birthDate;
    private String mobileNo;
    private String appType;
    private String memberRef;
    private String custContactTime;
    private String channel;
    private String module;
    
}
