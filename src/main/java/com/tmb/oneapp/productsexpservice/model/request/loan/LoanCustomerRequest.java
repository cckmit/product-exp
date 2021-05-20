package com.tmb.oneapp.productsexpservice.model.request.loan;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class LoanCustomerRequest {
    @NotNull
    private Long caID;
}
