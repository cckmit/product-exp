package com.tmb.oneapp.productsexpservice.activitylog.portfolio.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenPortfolioActivityLogRequest {

    private String scoreValue;

    private String nickname;

    private String purposeOfInvestment;

    private String receivingAccount;

    private String address;
}
