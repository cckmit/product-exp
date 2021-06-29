package com.tmb.oneapp.productsexpservice.model.request.flexiloan;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FlexiLoanConfirmRequest {

    @NotNull
    private Long caID;
    @NotEmpty
    private String productCode;
    @NotEmpty
    private String productNameTH;

}
