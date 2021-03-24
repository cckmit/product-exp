package com.tmb.oneapp.productsexpservice.model.response.investment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccDetailBody {
    private DetailFund detailFund;
}
