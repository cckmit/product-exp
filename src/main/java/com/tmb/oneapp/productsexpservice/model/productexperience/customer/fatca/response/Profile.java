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
public class Profile {

    @JsonAlias("rm_id")
    private String crmId;
}
