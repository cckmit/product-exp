package com.tmb.oneapp.productsexpservice.model.response.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchSourceOfIncome {
    @JsonProperty("ident_employment")
    public String identEmployment;
    @JsonProperty("src_of_inc_code")
    public String srcOfIncCode;
}
