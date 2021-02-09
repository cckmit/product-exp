package com.tmb.oneapp.productsexpservice.model.response.accdetail;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundAccountDetail {
    private FundRule fundRule;
    private AccountDetail accountDetail;
}
