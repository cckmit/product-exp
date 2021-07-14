package com.tmb.oneapp.productsexpservice.model.customer.creditcard.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreditCard {
    public String cardNo;
    public String accountId;
    public String cardImageAndroid;
    public String cardImageIos;
    public String productNameEn;
    public String productNameTh;
    public String productNickname;
    public String dueDate;
    public String cardType;
    public String cardName;
    public String updatedDate;
    public String compcode;
    public String dueAmount;
    public String creditSpend;
    public String creditRemain;
    public List<String> shortcuts;
    public String accountStatus;
    public String productOrder;
    public String supplementaryInfos;
}
