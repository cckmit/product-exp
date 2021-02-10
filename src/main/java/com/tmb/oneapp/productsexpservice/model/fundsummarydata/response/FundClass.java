package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response;

import lombok.Data;

import java.util.List;

@Data
public class FundClass {
    private String fundClassCode;
    private String fundClassNameEN;
    private String fundClassNameTH;
    private List<FundHouse> fundHouseList;
}
