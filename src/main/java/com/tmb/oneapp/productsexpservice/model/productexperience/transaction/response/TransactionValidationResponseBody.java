package com.tmb.oneapp.productsexpservice.model.productexperience.transaction.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionValidationResponseBody {

    private String orderDateTime;
    private String effectiveDate;
    private String workingHour;
    private Header header;
    private List<Consent> consentList;
    private String accountRedeem;
    private String frontEndFee;
    private String paymentDate;
}
