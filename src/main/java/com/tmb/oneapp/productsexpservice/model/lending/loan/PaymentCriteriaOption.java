package com.tmb.oneapp.productsexpservice.model.lending.loan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentCriteriaOption {
    private String optionId;
    private String optionNameTh;
    private String optionNameEn;
    @JsonIgnore
    private String entrySource;
}
