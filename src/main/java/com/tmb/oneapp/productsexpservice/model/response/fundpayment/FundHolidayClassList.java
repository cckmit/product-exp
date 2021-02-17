package com.tmb.oneapp.productsexpservice.model.response.fundpayment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundHolidayClassList {
    private String fundHouseCode;
    private String fundCode;
    private String holidayDate;
    private String holidayDesc;
}
