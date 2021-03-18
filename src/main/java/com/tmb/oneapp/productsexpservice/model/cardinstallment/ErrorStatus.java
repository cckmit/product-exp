package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Admin
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"error_code",
"description"
})
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ErrorStatus {
    @JsonProperty("error_code")
    private String errorCode;
    @JsonProperty("description")
    private String description;
}