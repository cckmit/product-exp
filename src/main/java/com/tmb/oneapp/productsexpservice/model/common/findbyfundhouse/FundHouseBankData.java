package com.tmb.oneapp.productsexpservice.model.common.findbyfundhouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundHouseBankData {

    @JsonIgnoreProperties(value = "fund_house_code")
    private String fundHouseCode;

    @JsonProperty(value = "fund_house_name_en")
    private String fundHouseNameEn;

    @JsonProperty(value="fund_house_name_th")
    private String fundHouseNameTh;

    @JsonProperty(value = "to_account_no")
    private String toAccountNo;

    @JsonProperty(value = "account_type")
    private String accountType;

    @JsonProperty(value = "financial_id")
    private String financialId;

    @JsonProperty(value = "ltf_merchant_id")
    private String ltfMerchantId;

    @JsonProperty(value = "rmf_merchant_id")
    private String rmfMerchantId;

}


