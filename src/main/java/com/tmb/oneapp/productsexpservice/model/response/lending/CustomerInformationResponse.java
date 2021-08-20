package com.tmb.oneapp.productsexpservice.model.response.lending;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CustomerInformationResponse {
    
	private String fullName;
    private String citizenIdOrPassportNo;
    private String birthDate;
    private String mobileNo;
    private String productName;
    private String memberRef;
    private String custContactTime;
    private String channel;
    private String module;
    private String createDate;
    private String appRefNo;
    
}
