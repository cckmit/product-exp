package com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fatca {

    @JsonAlias("fatca_date")
    private String fatcaDate;

    @JsonAlias("fatca_flag")
    private String fatcaFlag;
}
