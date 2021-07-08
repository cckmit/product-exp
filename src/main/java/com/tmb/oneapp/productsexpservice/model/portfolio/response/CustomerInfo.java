package com.tmb.oneapp.productsexpservice.model.portfolio.response;

import com.tmb.oneapp.productsexpservice.model.customer.CustomerModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerInfo extends CustomerModel {
    private String customerFirstNameEn;

    private String customerLastNameEn;

    private String customerFirstNameTh;

    private String customerLastNameTh;

    private String customerRiskLevel;

    private String contactAddress;

    private String registerAddress;

    private String officeAddress;
}
