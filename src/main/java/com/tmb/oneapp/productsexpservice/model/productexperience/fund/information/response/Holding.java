package com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Holding {

    private String name;

    private String weight;
}
