package com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @NotBlank
    private String accountId;

    @NotBlank
    private String accountType;

    private String fiId;

    private String ltfMerchantId;

    private String rmfMerchantId;

}
