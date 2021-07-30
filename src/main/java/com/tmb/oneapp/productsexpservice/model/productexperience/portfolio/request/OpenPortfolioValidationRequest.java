package com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenPortfolioValidationRequest {
    @JsonAlias({"existingCustomer", "isExistingCustomer"})
    private boolean existingCustomer;
}
