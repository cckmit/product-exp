package com.tmb.oneapp.productsexpservice.model.loan;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "flage_preload"
})
public class PreloadResponse {
    @JsonProperty("flage_preload")
    private boolean flagePreload;

}
