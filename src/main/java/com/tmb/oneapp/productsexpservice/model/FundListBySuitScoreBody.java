package com.tmb.oneapp.productsexpservice.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundListBySuitScoreBody {

    @JsonAlias("fundClassList")
    private List<FundClassListInfo> fundClassList;

}
