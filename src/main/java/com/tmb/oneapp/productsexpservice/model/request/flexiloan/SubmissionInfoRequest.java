package com.tmb.oneapp.productsexpservice.model.request.flexiloan;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionInfoRequest {

    @NotNull
    private Long caId;
    @NotEmpty
    private String productCode;

}
