package com.tmb.oneapp.productsexpservice.model.response.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SearchResponse {
    @JsonProperty("rm_id")
    public String rmId;

    @JsonProperty("id_no")
    public String idNo;

    @JsonProperty("customer_type")
    public String customerType;

    @JsonProperty("customer_name_en")
    public String customerNameEn;

    @JsonProperty("customer_name_th")
    public String customerNameTh;

    @JsonProperty("customer_first_name_en")
    public String customerFirstNameEn;

    @JsonProperty("customer_last_name_en")
    public String customerLastNameEn;

    @JsonProperty("customer_first_name_th")
    public String customerFirstNameTh;

    @JsonProperty("customer_last_name_th")
    public String customerLastNameTh;

    @JsonProperty("nationality")
    public String nationality;

    @JsonProperty("phone_no_full")
    public String phoneNoFull;

    @JsonProperty("gender")
    public String gender;

    @JsonProperty("marital_status")
    public String maritalStatus;

    @JsonProperty("mobile_number")
    public String mobileNumber;

    @JsonProperty("email")
    public String email;

    @JsonProperty("email_verify_status")
    public String emailVerifyStatus;

    @JsonProperty("email_type")
    public String emailType;

    @JsonProperty("birth_date")
    public String birthDate;

    @JsonProperty("issue_date")
    public String issueDate;

    @JsonProperty("expiry_date")
    public String expiryDate;

    @JsonProperty("contact_address")
    public String contactAddress;

    @JsonProperty("register_address")
    public String registerAddress;

    @JsonProperty("office_address")
    public String officeAddress;

    @JsonProperty("home_phone_no")
    public String homePhoneNo;

    @JsonProperty("office_phone_no")
    public String officePhoneNo;

    @JsonProperty("ekyc_flag")
    public String ekycFlag;

    @JsonProperty("kyc_limited_flag")
    public String kycLimitedFlag;

    @JsonProperty("kyc_block_date")
    public String kycBlockDate;

    @JsonProperty("kyc_last_update_date")
    public String kycLastUpdateDate;

    @JsonProperty("ekyc_identify_assurance_level")
    public String ekycIdentifyAssuranceLevel;

    @JsonProperty("pdpa_flag")
    public String pdpaFlag;

    @JsonProperty("pdpa_accepted_version")
    public String pdpaAcceptedVersion;

    @JsonProperty("pdpa_from_channel")
    public String pdpaFromChannel;

    @JsonProperty("pdpa_last_updated_date")
    public String pdpaLastUpdatedDate;

    @JsonProperty("data_analytic_flag")
    public String dataAnalyticFlag;

    @JsonProperty("data_analytic_accepted_version")
    public String dataAnalyticAcceptedVersion;

    @JsonProperty("data_analytic_from_channel")
    public String dataAnalyticFromChannel;

    @JsonProperty("data_analytic_last_updated_date")
    public String dataAnalyticLastUpdatedDate;

    @JsonProperty("market_conduct_flag")
    public String marketConductFlag;

    @JsonProperty("market_conduct_accepted_version")
    public String marketConductAcceptedVersion;

    @JsonProperty("market_conduct_from_channel")
    public String marketConductFromChannel;

    @JsonProperty("market_conduct_last_updated_date")
    public String marketConductLastUpdatedDate;

    @JsonProperty("occupation_code")
    public String occupationCode;

    @JsonProperty("business_type_desc")
    public String businessTypeDesc;

    @JsonProperty("salary")
    public Object salary;

    @JsonProperty("working_place")
    public String workingPlace;

    @JsonProperty("country_of_income")
    public String countryOfIncome;

    @JsonProperty("source_of_incomes")
    public List<SearchSourceOfIncome> sourceOfIncomes;

    @JsonProperty("fatca_flag")
    public String fatcaFlag;
}
