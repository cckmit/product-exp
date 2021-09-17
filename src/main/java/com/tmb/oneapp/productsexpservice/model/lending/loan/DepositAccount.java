package com.tmb.oneapp.productsexpservice.model.lending.loan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DepositAccount {
    private String productCode;
    private String productNickname;
    private String productNameTh;
    private String productNameEn;
    private String accountNumber;
    private String accountStatus;
    private String accountType;
    private String accountName;
    private String branchCode;
    private String relationshipCode;
    private String allowFromForBillPayTopUpEpayment;
    private String allowTransferFromAccount;
    private String allowSetQuickBalance;
    private String allowPayLoanDirectDebit;
    private String allowReceiveLoanFund;
    private String waiveFeeForBillpay;
    private String waiveFeeForPromptPay;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal availableBalance;

    private String productIconUrl;
    private String productConfigSortOrder;

}
