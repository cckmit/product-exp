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

    public String building;

    @JsonProperty("company_name")
    public String companyName;

    public String country;

    public String district;

    public String moo;

    public String no;

    @JsonProperty("phone_extension")
    public String phoneExtension;

    @JsonProperty("phone_no")
    public String phoneNo;

    @JsonProperty("postal_code")
    public String postalCode;

    public String province;

    public String road;

    public String soi;

    @JsonProperty("sub_district")
    public String subDistrict;

}
