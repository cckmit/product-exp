package com.tmb.oneapp.productsexpservice.model.common.findbyfundhouse;

import com.tmb.oneapp.productsexpservice.model.response.Status;
import lombok.Data;

@Data
public class FundHouseResponse {
    private Status status;
    private FundHouseBankData data;
}
