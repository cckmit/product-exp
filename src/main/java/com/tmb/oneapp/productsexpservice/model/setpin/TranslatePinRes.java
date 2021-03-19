package com.tmb.oneapp.productsexpservice.model.setpin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TranslatePinRes {

@JsonProperty("result")
private Result result;
@JsonProperty("amProcessingTimeMillis")
private Integer amProcessingTimeMillis;
}
