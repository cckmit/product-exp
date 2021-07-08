package com.tmb.oneapp.productsexpservice.model.portfolio.request;

import com.tmb.oneapp.productsexpservice.model.client.CustomerClientModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OpenPortfolioRequestBody extends CustomerClientModel {

    /* open.portfolio */
    private String suitabilityScore;

    private String portfolioType;

    private String purposeTypeCode;

    private String portfolioNickName;
}
