package com.tmb.oneapp.productsexpservice.model.client.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipRequest {

    private String crmId;

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
