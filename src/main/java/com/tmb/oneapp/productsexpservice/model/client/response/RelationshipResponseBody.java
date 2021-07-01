package com.tmb.oneapp.productsexpservice.model.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipResponseBody {

    private String jointType;

    private String preferredRedemptionAccountCode;

    private String preferredRedemptionAccountName;

    private String preferredSubscriptionAccountCode;

    private String preferredSubscriptionAccountName;

    private String registeredForVat;

    private String vatEstablishmentBranchCode;

    private String withHoldingTaxPreference;

    private String status;

    private String preferredAddressType;

    private String crmId;

    private String code;

    private String nature;

    private String typeCode;

    private String mgtBegin;

    private String branchReference;

    private String customerType;

    private String financialAdvanceReference;

    private String riskProfile;

    private String roboStatus;

    private String ownerShipRule;

    private String portfolioAdvanceTypeCode;
}
