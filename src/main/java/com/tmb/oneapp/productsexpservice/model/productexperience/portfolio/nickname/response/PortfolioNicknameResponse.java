package com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.response;

import com.tmb.oneapp.productsexpservice.model.response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioNicknameResponse {

    private Status status;

    private PortfolioNicknameResponseBody data;
}
