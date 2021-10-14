package com.tmb.oneapp.productsexpservice.model.response.fundpayment;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepositAccount {

    private String productNickname;

    private String productNameTH;

    private String productNameEN;

    private String accountNumber;

    private String accountStatus;

    private String accountStatusCode;

    private String accountType;

    private String accountTypeShort;

    private BigDecimal availableBalance;

    private String FiID;

}
