package com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.response.investment.FundDetail;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDetail extends FundDetail {

    private List<FundOrderHistory> ordersHistories;
}
