package com.tmb.oneapp.productsexpservice.model.productexperience.fund.portfolio.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponse {

    private List<PortfolioResponseBody> portfolioResponseBody;
}
