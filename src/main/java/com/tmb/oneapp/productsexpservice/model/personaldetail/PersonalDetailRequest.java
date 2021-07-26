package com.tmb.oneapp.productsexpservice.model.personaldetail;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PersonalDetailRequest {
    @NotNull
    private Long caId;
}
