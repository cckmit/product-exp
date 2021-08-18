package com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response;

import com.tmb.oneapp.productsexpservice.model.productexperience.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.response.PortfolioNicknameResponseBody;
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

    private OccupationResponseBody occupationResponse;
}
