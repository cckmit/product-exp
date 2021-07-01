package com.tmb.oneapp.productsexpservice.model.portfolio.response;

import com.tmb.oneapp.productsexpservice.model.response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenPortfolioResponse {

    private Status status;

    private OpenPortfolioResponseBody data;
}
