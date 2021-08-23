package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundSearch {

    private String portfolioNumber;

    private String fundCode;

    private String fundNameEN;

    private String fundNameTH;

    private String fundNickNameEN;

    private String fundNickNameTH;

    private String fundShortName;

    private String fundHouseCode;
}
