package com.tmb.oneapp.productsexpservice.model.portdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Port {
    @JsonProperty("appl_code")
    private String applCode ;
    @JsonProperty("acct_ctrl1")
    private String acctCtrl1 ;
    @JsonProperty("acct_ctrl2")
    private String acctCtrl2 ;
    @JsonProperty("acct_ctrl3")
    private String acctCtrl3 ;
    @JsonProperty("acct_ctrl4")
    private String acctCtrl4 ;
    @JsonProperty("acct_nbr")
    private String acctNbr ;
    @JsonProperty("account_name")
    private String accountName ;
    @JsonProperty("product_group_code")
    private String productGroupCode ;
    @JsonProperty("product_code")
    private String productCode ;
    @JsonProperty("owner_type")
    private String ownerType ;
    @JsonProperty("relationship_code")
    private String relationshipCode ;
    @JsonProperty("current_balance")
    private BigDecimal currentBalance ;
    @JsonProperty("balance_currency")
    private String balanceCurrency ;


}
