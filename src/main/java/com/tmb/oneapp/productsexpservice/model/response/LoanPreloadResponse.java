package com.tmb.oneapp.productsexpservice.model.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanPreloadResponse {
    private Boolean flagePreload;

}
