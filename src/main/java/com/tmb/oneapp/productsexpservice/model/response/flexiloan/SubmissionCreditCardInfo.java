package com.tmb.oneapp.productsexpservice.model.response.flexiloan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SubmissionCreditCardInfo {
    private String paymentMethod;
    private String featureType;
    private String eStatement;
    private String otherBank;
    private String otherBankInProgress;
}

