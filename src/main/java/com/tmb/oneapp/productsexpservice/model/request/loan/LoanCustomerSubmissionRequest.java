package com.tmb.oneapp.productsexpservice.model.request.loan;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class LoanCustomerSubmissionRequest {
    @NotEmpty
    private String featureType;
    @NotNull
    private BigDecimal requestAmount;
    @NotEmpty
    private String installment;
    @NotEmpty
    private String disburstAccountNo;

    @NotEmpty
    private String disburstAccountName;

    @NotEmpty
    private String bankName;

    @NotNull
    private Long caID;


}
