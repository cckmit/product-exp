package com.tmb.oneapp.productsexpservice.mapper.portfolio;

import com.tmb.oneapp.productsexpservice.model.productexperience.client.request.RelationshipRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequestBody;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OpenPortfolioMapper {

    RelationshipRequest openPortfolioRequestBodyToRelationshipRequest(OpenPortfolioRequestBody openPortfolioRequestBody);

    OpenPortfolioRequest openPortfolioRequestBodyToOpenPortfolioRequest(OpenPortfolioRequestBody openPortfolioRequestBody);
}
