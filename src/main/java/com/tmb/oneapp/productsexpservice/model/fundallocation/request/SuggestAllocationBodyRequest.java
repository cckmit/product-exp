package com.tmb.oneapp.productsexpservice.model.fundallocation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
public class SuggestAllocationBodyRequest {
    private String crmId;
}
