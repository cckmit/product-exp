package com.tmb.oneapp.productsexpservice.model.personaldetail;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Address {
    private String addrTypCode;
    private String no;
    private String buildingName;
    private String streetName;
    private String postalCode;
    private String country;
    private String tumbol;
    private String road;
    private String moo;
    private String amphur;
    private String province;
    private String floor;
    private String roomNo;
}
