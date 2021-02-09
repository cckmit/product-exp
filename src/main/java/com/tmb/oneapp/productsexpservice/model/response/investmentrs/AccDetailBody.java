package com.tmb.oneapp.productsexpservice.model.response.investmentrs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccDetailBody {
    private OrderToBeProcess orderToBeProcess;
    private DetailFund detailFund;
}
