package com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.purpose.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountPurpose {

    private String purposeCode;

    private String purposeEnglishDescription;

    private String purposeThaiDescription;
}
