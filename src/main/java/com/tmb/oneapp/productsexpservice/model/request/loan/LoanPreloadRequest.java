package com.tmb.oneapp.productsexpservice.model.request.loan;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class LoanPreloadRequest {
    @NotNull
    private int crmId;
    @NotNull
    private int appId;
    @NotEmpty
    private String channel;
}
