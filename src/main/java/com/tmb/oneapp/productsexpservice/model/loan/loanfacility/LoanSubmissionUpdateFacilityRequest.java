package com.tmb.oneapp.productsexpservice.model.loan.loanfacility;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class LoanSubmissionUpdateFacilityRequest {

    @NotNull
    private Long caID;

}
