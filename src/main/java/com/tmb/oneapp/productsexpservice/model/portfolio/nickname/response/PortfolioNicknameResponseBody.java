package com.tmb.oneapp.productsexpservice.model.portfolio.nickname.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioNicknameResponseBody {

    private String portfolioNumber;

    private String portfolioNickName;

    private String oldPortNickName;
}
