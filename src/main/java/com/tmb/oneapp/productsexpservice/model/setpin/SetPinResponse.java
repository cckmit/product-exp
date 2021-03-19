package com.tmb.oneapp.productsexpservice.model.setpin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SetPinResponse {
    private SilverlakeStatus status;
}
