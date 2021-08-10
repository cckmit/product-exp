package com.tmb.oneapp.productsexpservice.model.customer.calculaterisk.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressModel {

    private String building;

    @JsonProperty("company_name")
    private String companyName;

    private String country;

    private String district;

    private String moo;

    private String no;

    @JsonProperty("phone_extension")
    private String phoneExtension;

    @JsonProperty("phone_no")
    private String phoneNo;

    @JsonProperty("postal_code")
    private String postalCode;

    private String province;

    private String road;

    private String soi;

    @JsonProperty("sub_district")
    private String subDistrict;

}
