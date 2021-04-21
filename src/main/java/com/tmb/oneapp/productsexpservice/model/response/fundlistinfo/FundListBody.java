package com.tmb.oneapp.productsexpservice.model.response.fundlistinfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundListBody {
    private List<FundClassListInfo> fundClassList;
}
