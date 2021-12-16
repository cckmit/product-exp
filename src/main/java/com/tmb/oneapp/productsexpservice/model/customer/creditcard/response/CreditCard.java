package com.tmb.oneapp.productsexpservice.model.customer.creditcard.response;

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
public class CreditCard {

    @JsonAlias("card_no")
    private String cardNo;

    @JsonAlias("account_id")
    private String accountId;

    private String accountType;

    @JsonAlias("card_image_android")
    private String cardImageAndroid;

    @JsonAlias("card_image_ios")
    private String cardImageIos;

    @JsonAlias("product_name_en")
    private String productNameEN;

    @JsonAlias("product_name_th")
    private String productNameTH;

    @JsonAlias("product_nickname")
    private String productNickname;

    @JsonAlias("due_date")
    private String dueDate;

    @JsonAlias("card_type")
    private String cardType;

    @JsonAlias("card_name")
    private String cardName;

    @JsonAlias("updated_date")
    private String updatedDate;

    private String compcode;

    @JsonAlias("due_amount")
    private String dueAmount;

    @JsonAlias("credit_spend")
    private String creditSpend;

    @JsonAlias("credit_remain")
    private String creditRemain;

    private List<Shortcut> shortcuts;

    @JsonAlias("account_status")
    private String accountStatus;

    @JsonAlias("product_order")
    private String productOrder;

    @JsonAlias("supplementary_infos")
    private List<SupplementaryInfo> supplementaryInfos;

    @JsonAlias("product_code")
    private String productCode;

}
