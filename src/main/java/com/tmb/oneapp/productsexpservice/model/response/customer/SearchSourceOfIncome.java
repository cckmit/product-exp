package com.tmb.oneapp.productsexpservice.model.response.customer;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @JsonAlias({"ident_employment"})
    public String identifierEmployment;

    @JsonAlias({"src_of_inc_code"})
    public String srcOfIncCode;
}
