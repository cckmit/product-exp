package com.tmb.oneapp.productsexpservice.dto.fund.dca.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DcaValidationDto {
    private String factsheetData;
}
