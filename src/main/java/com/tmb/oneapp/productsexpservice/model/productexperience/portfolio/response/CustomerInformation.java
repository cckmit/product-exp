package com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response;

import com.tmb.oneapp.productsexpservice.model.productexperience.customer.CustomerModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerInformation extends CustomerModel {

    private String customerFirstNameEn;

    private String customerLastNameEn;

    private String customerFirstNameTh;

    private String customerLastNameTh;

    private String customerRiskLevel;

    private String contactAddress;

    private String registerAddress;

    private String officeAddress;
}
