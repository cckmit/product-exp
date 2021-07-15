package com.tmb.oneapp.productsexpservice.model.customer.creditcard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditcardInformationRequestBody {
    @NotNull
    private String crmId;
}
