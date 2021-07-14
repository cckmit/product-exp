package com.tmb.oneapp.productsexpservice.model.productexperience.customer;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
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

    private String nationality;

    @JsonAlias({"nationality_2"})
    private String nationalitySecond;

    private String customerThaiName;

    private String customerEnglishName;
}
