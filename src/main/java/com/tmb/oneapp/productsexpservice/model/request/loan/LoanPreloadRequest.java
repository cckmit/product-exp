package com.tmb.oneapp.productsexpservice.model.request.loan;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class LoanPreloadRequest {
    @NotEmpty
    private String crmId;
    @NotEmpty
    private String productCode;
    @NotEmpty
    private String search;
}
