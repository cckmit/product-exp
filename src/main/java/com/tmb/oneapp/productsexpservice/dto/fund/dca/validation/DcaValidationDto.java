package com.tmb.oneapp.productsexpservice.dto.fund.dca.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DcaValidationDto {

    @JsonProperty(value = "factsheetData")
    private String factSheetData;
}
