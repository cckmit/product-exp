package com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.response.investment.FundDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDetail extends FundDetail {

    private List<FundOrderHistory> ordersHistories;
}
