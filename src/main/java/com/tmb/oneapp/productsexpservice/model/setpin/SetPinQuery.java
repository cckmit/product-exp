package com.tmb.oneapp.productsexpservice.model.setpin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SetPinQuery {
    @JsonProperty("account_id")
    @ApiModelProperty(notes = "accountId", required=true, example="0000000050078690471000960")
    private String accountId;
    @JsonProperty("pin")
    @ApiModelProperty(notes = "pin", required=true, example="FE19C46DC8AD9F61")
    private String pin;
}
