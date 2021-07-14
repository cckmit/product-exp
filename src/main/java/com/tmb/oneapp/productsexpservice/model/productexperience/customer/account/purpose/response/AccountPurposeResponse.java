package com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.purpose.response;

import com.tmb.oneapp.productsexpservice.model.response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountPurposeResponse {

    private Status status;

    private AccountPurposeResponseBody data;
}
