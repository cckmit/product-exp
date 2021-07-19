package com.tmb.oneapp.productsexpservice.dto.fund.dcainformation;

import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
