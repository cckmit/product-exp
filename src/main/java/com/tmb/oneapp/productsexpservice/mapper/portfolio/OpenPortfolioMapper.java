package com.tmb.oneapp.productsexpservice.mapper.portfolio;

import com.tmb.oneapp.productsexpservice.model.client.request.RelationshipRequestBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.nickname.request.PortfolioNicknameRequestBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioRequestBody;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OpenPortfolioMapper {

    RelationshipRequestBody openPortfolioRequestToRelationshipRequestBody(OpenPortfolioRequest openPortfolioRequest);

    OpenPortfolioRequestBody openPortfolioRequestToOpenPortfolioRequestBody(OpenPortfolioRequest openPortfolioRequest);

    PortfolioNicknameRequestBody openPortfolioRequestToPortfolioNicknameRequestBody(OpenPortfolioRequest openPortfolioRequest);
}
