package com.tmb.oneapp.productsexpservice.model.productexperience.fund.portfolio.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponseBody {

    private String portfolioNumber;

    private String nickname;

    private String jointFlag;
}
