package com.tmb.oneapp.productsexpservice.model.portfolio.nickname.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioNicknameRequestBody {

    private String portfolioNumber;

    private String portfolioNickName;
}
