package com.tmb.oneapp.productsexpservice.model.request.loan;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.protocol.types.Field;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
