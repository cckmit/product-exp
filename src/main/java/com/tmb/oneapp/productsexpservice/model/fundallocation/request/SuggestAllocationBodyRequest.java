package com.tmb.oneapp.productsexpservice.model.fundallocation.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SuggestAllocationBodyRequest {
    private @NotNull String crmId;
}
