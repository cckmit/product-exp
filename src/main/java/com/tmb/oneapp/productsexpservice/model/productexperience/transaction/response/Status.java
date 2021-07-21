package com.tmb.oneapp.productsexpservice.model.productexperience.transaction.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    @JsonProperty(value = "statusCode")
    private String code;

    @JsonProperty(value = "statusDesc")
    private String description;

    private String severity;
}
