package com.tmb.oneapp.productsexpservice.model.customer.request;

import com.tmb.oneapp.productsexpservice.model.customer.CustomerModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerRequest extends CustomerModel {

}
