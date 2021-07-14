package com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioNicknameRequest {

    private String portfolioNumber;

    private String portfolioNickName;
}
