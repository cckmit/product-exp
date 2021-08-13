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
    private String businessCode;

    @JsonProperty("card_id")
    private String cardId;

    @JsonProperty("dob")
    private String dob;

    @JsonProperty("dob_country")
    private String dobCountry;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("first_name_eng")
    private String firstNameEng;

    @JsonProperty("income_source_country")
    private String incomeSourceCountry;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("last_name_eng")
    private String lastNameEng;

    @JsonProperty("occupation_code")
    private String occupationCode;

    @JsonProperty("office_address")
    private AddressModel officeAddress;

    @JsonProperty("primary_address")
    private AddressModel primaryAddress;

    @JsonProperty("registered_address")
    private AddressModel registeredAddress;

}
