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
public class Consent {

    private String code;

    @JsonProperty(value = "messageTh")
    private String thaiMessage;

    @JsonProperty(value = "messageEn")
    private String englishMessage;
}
