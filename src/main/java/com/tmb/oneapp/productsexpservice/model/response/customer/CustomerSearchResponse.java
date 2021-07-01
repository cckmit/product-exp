package com.tmb.oneapp.productsexpservice.model.response.customer;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    public String rmId;

    @JsonAlias("id_no")
    public String idNo;

    @JsonAlias("customer_type")
    public String customerType;

    @JsonAlias("customer_name_en")
    public String customerNameEn;

    @JsonAlias("customer_name_th")
    public String customerNameTh;

    @JsonAlias("customer_first_name_en")
    public String customerFirstNameEn;

    @JsonAlias("customer_last_name_en")
    public String customerLastNameEn;

    @JsonAlias("customer_first_name_th")
    public String customerFirstNameTh;

    @JsonAlias("customer_last_name_th")
    public String customerLastNameTh;

    @JsonAlias("nationality")
    public String nationality;

    @JsonAlias("phone_no_full")
    public String phoneNoFull;

    @JsonAlias("gender")
    public String gender;

    @JsonAlias("marital_status")
    public String maritalStatus;

    @JsonAlias("mobile_number")
    public String mobileNumber;

    @JsonAlias("email")
    public String email;

    @JsonAlias("email_verify_status")
    public String emailVerifyStatus;

    @JsonAlias("email_type")
    public String emailType;

    @JsonAlias("birth_date")
    public String birthDate;

    @JsonAlias("issue_date")
    public String issueDate;

    @JsonAlias("expiry_date")
    public String expiryDate;

    @JsonAlias("contact_address")
    public String contactAddress;

    @JsonAlias("register_address")
    public String registerAddress;

    @JsonAlias("office_address")
    public String officeAddress;

    @JsonAlias("home_phone_no")
    public String homePhoneNo;

    @JsonAlias("office_phone_no")
    public String officePhoneNo;

    @JsonAlias("ekyc_flag")
    public String ekycFlag;

    @JsonAlias("kyc_limited_flag")
    public String kycLimitedFlag;

    @JsonAlias("kyc_block_date")
    public String kycBlockDate;

    @JsonAlias("kyc_last_update_date")
    public String kycLastUpdateDate;

    @JsonAlias("ekyc_identify_assurance_level")
    public String ekycIdentifyAssuranceLevel;

    @JsonAlias("pdpa_flag")
    public String pdpaFlag;

    @JsonAlias("pdpa_accepted_version")
    public String pdpaAcceptedVersion;

    @JsonAlias("pdpa_from_channel")
    public String pdpaFromChannel;

    @JsonAlias("pdpa_last_updated_date")
    public String pdpaLastUpdatedDate;

    @JsonAlias("data_analytic_flag")
    public String dataAnalyticFlag;

    @JsonAlias("data_analytic_accepted_version")
    public String dataAnalyticAcceptedVersion;

    @JsonAlias("data_analytic_from_channel")
    public String dataAnalyticFromChannel;

    @JsonAlias("data_analytic_last_updated_date")
    public String dataAnalyticLastUpdatedDate;

    @JsonAlias("market_conduct_flag")
    public String marketConductFlag;

    @JsonAlias("market_conduct_accepted_version")
    public String marketConductAcceptedVersion;

    @JsonAlias("market_conduct_from_channel")
    public String marketConductFromChannel;

    @JsonAlias("market_conduct_last_updated_date")
    public String marketConductLastUpdatedDate;

    @JsonAlias("occupation_code")
    public String occupationCode;

    @JsonAlias("business_type_desc")
    public String businessTypeDesc;

    @JsonAlias("salary")
    public Object salary;

    @JsonAlias("working_place")
    public String workingPlace;

    @JsonAlias("country_of_income")
    public String countryOfIncome;

    @JsonAlias("source_of_incomes")
    public List<SearchSourceOfIncome> sourceOfIncomes;

    @JsonAlias("fatca_flag")
    public String fatcaFlag;

    @JsonAlias("amloFlag")
    public String amloFlag;

    @JsonAlias("customer_risk_level")
    public String customerRiskLevel;
}
