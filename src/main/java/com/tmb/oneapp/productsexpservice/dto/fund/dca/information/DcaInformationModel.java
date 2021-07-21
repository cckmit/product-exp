package com.tmb.oneapp.productsexpservice.dto.fund.dca.information;

import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DcaInformationModel extends FundClassListInfo {
    private String portfolioNumber;
    private BigDecimal unrealizedProfit;
    private BigDecimal unrealizedProfitPercent;
    private String marketValue;
}
