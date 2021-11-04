package com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fee {

    private String billPmtFee;

    private String feeType;

    private String paymentFee;
}
