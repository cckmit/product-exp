package com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.oneapp.productsexpservice.model.response.customer.SearchSourceOfIncome;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerSearchResponse {

    @JsonAlias("rm_id")
    private String crmId;

    @JsonAlias({"id_no"})
    private String idNumber;

    @JsonAlias({"customer_type"})
    private String customerType;

    @JsonAlias({"customer_name_en"})
    private String customerEnglishName;

    @JsonAlias({"customer_name_th"})
    private String customerThaiName;

    @JsonAlias({"customer_first_name_en"})
    private String customerEnglishFirstName;

    @JsonAlias({"customer_last_name_en"})
    private String customerEnglishLastName;

    @JsonAlias({"customer_first_name_th"})
    private String customerThaiFirstName;

    @JsonAlias({"customer_last_name_th"})
    private String customerThaiLastName;

    private String nationality;

    @JsonAlias({"phone_no_full"})
    private String phoneNumberFull;

    private String gender;

    @JsonAlias({"marital_status"})
    private String maritalStatus;

    @JsonAlias({"mobile_number"})
    private String mobileNumber;

    private String email;

    @JsonAlias({"email_verify_status"})
    private String emailVerifyStatus;

    @JsonAlias({"email_type"})
    private String emailType;

    @JsonAlias({"birth_date"})
    private String birthDate;

    @JsonAlias({"issue_date"})
    private String issueDate;

    @JsonAlias({"expiry_date"})
    private String expiryDate;

    @JsonAlias({"contact_address"})
    private String contactAddress;

    @JsonAlias({"register_address"})
    private String registerAddress;

    @JsonAlias({"office_address"})
    private String officeAddress;

    @JsonAlias({"home_phone_no"})
    private String homePhoneNumber;

    @JsonAlias({"office_phone_no"})
    private String officePhoneNumber;

    @JsonAlias({"ekyc_flag"})
    private String ekycFlag;

    @JsonAlias({"kyc_limited_flag"})
    private String kycLimitedFlag;

    @JsonAlias({"kyc_block_date"})
    private String kycBlockDate;

    @JsonAlias({"kyc_last_update_date"})
    private String kycLastUpdateDate;

    @JsonAlias({"ekyc_identify_assurance_level"})
    private String ekycIdentifyAssuranceLevel;

    @JsonAlias({"pdpa_flag"})
    private String pdpaFlag;

    @JsonAlias({"pdpa_accepted_version"})
    private String pdpaAcceptedVersion;

    @JsonAlias({"pdpa_from_channel"})
    private String pdpaFromChannel;

    @JsonAlias({"pdpa_last_updated_date"})
    private String pdpaLastUpdatedDate;

    @JsonAlias({"data_analytic_flag"})
    private String dataAnalyticFlag;

    @JsonAlias({"data_analytic_accepted_version"})
    private String dataAnalyticAcceptedVersion;

    @JsonAlias({"data_analytic_from_channel"})
    private String dataAnalyticFromChannel;

    @JsonAlias({"data_analytic_last_updated_date"})
    private String dataAnalyticLastUpdatedDate;

    @JsonAlias({"market_conduct_flag"})
    private String marketConductFlag;

    @JsonAlias({"market_conduct_accepted_version"})
    private String marketConductAcceptedVersion;

    @JsonAlias({"market_conduct_from_channel"})
    private String marketConductFromChannel;

    @JsonAlias({"market_conduct_last_updated_date"})
    private String marketConductLastUpdatedDate;

    @JsonAlias({"occupation_code"})
    private String occupationCode;

    @JsonAlias({"business_type_desc"})
    private String businessTypeDescription;

    private Object salary;

    @JsonAlias({"working_place"})
    private String workingPlace;

    @JsonAlias({"country_of_income"})
    private String countryOfIncome;

    @JsonAlias({"source_of_incomes"})
    private List<SearchSourceOfIncome> sourceOfIncomes;

    @JsonAlias({"fatca_flag"})
    private String fatcaFlag;

    @JsonAlias("amlo_refuse_flag")
    private String amloFlag;

    @JsonAlias("customer_level")
    private String customerRiskLevel;

    @JsonAlias({"nationality_2"})
    private String nationalitySecond;

    @JsonProperty("business_type_code")
    private String businessTypeCode;

    @JsonProperty("office_address_data")
    private AddressWithPhone officeAddressData;

    @JsonProperty("primary_address_data")
    private AddressWithPhone primaryAddressData;

    @JsonProperty("registered_addressData")
    private AddressWithPhone registeredAddressData;

    @JsonProperty("full_fill_flag")
    private String fullFillFlag;

}
