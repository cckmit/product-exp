package com.tmb.oneapp.productsexpservice.model.openportfolio.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenPortfolioRequest {

    @JsonProperty(value = "rmId")
    private String crmId;
    @JsonAlias(value = "isExistingCustomer")
    private boolean existingCustomer;
}
