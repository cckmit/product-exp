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
    public String cardNo;

    @JsonAlias("account_id")
    public String accountId;

    @JsonAlias("card_image_android")
    public String cardImage_android;

    @JsonAlias("card_image_ios")
    public String cardImage_ios;

    @JsonAlias("product_name_en")
    public String productName_en;

    @JsonAlias("product_name_th")
    public String productName_th;

    @JsonAlias("product_nickname")
    public Object productNickname;

    @JsonAlias("due_date")
    public Object dueDate;

    @JsonAlias("card_type")
    public String cardType;

    @JsonAlias("card_name")
    public String cardName;

    @JsonAlias("updated_date")
    public String updatedDate;

    public String compcode;

    @JsonAlias("due_amount")
    public Object dueAmount;

    @JsonAlias("credit_spend")
    public String creditSpend;

    @JsonAlias("credit_remain")
    public String creditRemain;

    public List<Shortcut> shortcuts;

    @JsonAlias("account_status")
    public String accountStatus;

    @JsonAlias("product_order")
    public String productOrder;

    @JsonAlias("supplementary_infos")
    public List<SupplementaryInfo> supplementaryInfos;
}
