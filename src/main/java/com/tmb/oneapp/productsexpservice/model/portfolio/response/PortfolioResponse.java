package com.tmb.oneapp.productsexpservice.model.portfolio.response;

import com.tmb.oneapp.productsexpservice.model.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.nickname.response.PortfolioNicknameResponseBody;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponse {

    private RelationshipResponseBody relationshipResponse;

    private OpenPortfolioResponseBody openPortfolioResponse;

    private PortfolioNicknameResponseBody portfolioNicknameResponse;
}
