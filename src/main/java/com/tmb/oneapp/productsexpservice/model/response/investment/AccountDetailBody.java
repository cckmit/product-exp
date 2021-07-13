package com.tmb.oneapp.productsexpservice.model.response.investment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDetailBody {

    @JsonProperty(value = "detailFund")
    private FundDetail fundDetail;
}
