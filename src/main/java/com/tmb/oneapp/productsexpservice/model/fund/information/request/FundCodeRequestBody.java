package com.tmb.oneapp.productsexpservice.model.fund.information.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class FundCodeRequestBody {

    @JsonProperty(value = "fundCode")
    private String code;
}
