package com.tmb.oneapp.productsexpservice.model.productexperience.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerClientModel {

    private String jointType;

    private String preferredRedemptionAccountCode;

    private String preferredRedemptionAccountName;

    private String preferredSubscriptionAccountCode;

    private String preferredSubscriptionAccountName;

    private String registeredForVat;

    private String vatEstablishmentBranchCode;

    private String withHoldingTaxPreference;

    private String preferredAddressType;

    private String status;
}
