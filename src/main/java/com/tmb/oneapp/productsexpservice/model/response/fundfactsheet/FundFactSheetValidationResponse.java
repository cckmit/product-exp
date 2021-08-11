package com.tmb.oneapp.productsexpservice.model.response.fundfactsheet;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class FundFactSheetValidationResponse extends FundResponse {

    private FundFactSheetData body;
    
}
