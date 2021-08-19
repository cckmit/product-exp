package com.tmb.oneapp.productsexpservice.model.productexperience.customer.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.CustomerModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerRequest extends CustomerModel {
    @JsonAlias({"existingCustomer", "isExistingCustomer"})
    private boolean existingCustomer;
}
