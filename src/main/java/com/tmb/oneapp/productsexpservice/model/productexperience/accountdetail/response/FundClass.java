package com.tmb.oneapp.productsexpservice.model.productexperience.accountdetail.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundClass {

    private String fundClassCode;

    private String fundClassEnglishName;

    private String fundClassThaiName;

    private List<FundHouse> fundHouseList;
}
