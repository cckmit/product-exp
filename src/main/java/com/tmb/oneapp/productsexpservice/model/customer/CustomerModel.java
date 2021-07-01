package com.tmb.oneapp.productsexpservice.model.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerModel {
    private String crmId;

    private String wealthCrmId;

    private String phoneNumber;

    private String dateOfBirth;

    private String emailAddress;

    private String maritalStatus;

    private String residentGeoCode;

    private String taxNumber;

    private String branchCode;

    private String makerCode;

    private String kycFlag;

    private String amloFlag;

    private String lastDateSync;

    private String nationalDocumentExpireDate;

    private String nationalDocumentId;

    private String nationalDocumentIdentificationType;

    private String customerThaiName;

    private String customerEnglishName;
}
