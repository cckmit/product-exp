package com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressWithPhone {

    @JsonProperty("address_no")
    public String addressNo;

    @JsonProperty("build_village_name")
    public String buildVillageName;

    @JsonProperty("moo")
    public String moo;

    @JsonProperty("soi")
    public String soi;

    @JsonProperty("road")
    public String road;

    @JsonProperty("sub_district")
    public String subDistrict;

    @JsonProperty("district")
    public String district;

    @JsonProperty("province")
    public String province;

    @JsonProperty("postal_code")
    public String postalCode;

    @JsonProperty("country")
    public String country;

    @JsonProperty("working_place")
    public String workingPlace;

}
