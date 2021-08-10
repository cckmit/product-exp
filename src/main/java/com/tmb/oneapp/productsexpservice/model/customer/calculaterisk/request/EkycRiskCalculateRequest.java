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
public class EkycRiskCalculateRequest {
    @JsonProperty("business_code")
    public String businessCode;

    @JsonProperty("card_id")
    public String cardId;

    @JsonProperty("dob")
    public String dob;

    @JsonProperty("dob_country")
    public String dobCountry;

    @JsonProperty("first_name")
    public String firstName;

    @JsonProperty("first_name_eng")
    public String firstNameEng;

    @JsonProperty("income_source_country")
    public String incomeSourceCountry;

    @JsonProperty("last_name")
    public String lastName;

    @JsonProperty("last_name_eng")
    public String lastNameEng;

    @JsonProperty("occupation_code")
    public String occupationCode;

    @JsonProperty("office_address")
    public AddressModel officeAddress;

    @JsonProperty("primary_address")
    public AddressModel primaryAddress;

    @JsonProperty("registered_address")
    public AddressModel registeredAddress;

}
