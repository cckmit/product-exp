package com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private String accountId;

    private String accountType;

    private String fiId;

    private String ltfMerchantId;

    private String rmfMerchantId;
}
