package com.tmb.oneapp.productsexpservice.model.response.fundholiday;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundHolidayClassList;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundHolidayBody {
    @JsonProperty("fundClassList")
    private List<FundHolidayClassList> fundClassList;

}
