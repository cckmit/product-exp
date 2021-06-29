package com.tmb.oneapp.productsexpservice.dto.fund.fundallocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MutualFund {
    private String fundClassCode;
    private String fundClassNameTH;
    private String fundClassNameEN;
    private String fundClassPercent;
}
