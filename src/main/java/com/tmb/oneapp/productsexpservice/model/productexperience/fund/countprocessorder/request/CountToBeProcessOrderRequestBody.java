package com.tmb.oneapp.productsexpservice.model.productexperience.fund.countprocessorder.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountToBeProcessOrderRequestBody {

    @NotBlank
    private String serviceType;
}
