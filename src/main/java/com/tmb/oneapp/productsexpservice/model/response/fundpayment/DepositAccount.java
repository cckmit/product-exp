package com.tmb.oneapp.productsexpservice.model.response.fundpayment;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepositAccount {
    private String productNickname;
    private String productNameTH;
    private String productNameEN;
    private String accountNumber;
    private String accountStatus;
    private String accountType;
    private BigDecimal availableBalance;
}
