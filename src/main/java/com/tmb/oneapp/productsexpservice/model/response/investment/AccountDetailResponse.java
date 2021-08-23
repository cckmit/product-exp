package com.tmb.oneapp.productsexpservice.model.response.investment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDetailResponse {

    private FundDetail fundDetail;
}
