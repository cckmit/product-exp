package com.tmb.oneapp.productsexpservice.model.portdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PortData {
    @JsonProperty("mutual_fund_accounts")
    private List<Port>  mutualFundAccounts;

}
