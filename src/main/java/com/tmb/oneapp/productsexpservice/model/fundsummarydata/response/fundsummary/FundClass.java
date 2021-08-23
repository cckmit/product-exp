package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundClass {

    private String fundClassCode;

    private String fundClassNameEN;

    private String fundClassNameTH;

    private List<FundHouse> fundHouseList;

    private String fundClassPercent;
}
