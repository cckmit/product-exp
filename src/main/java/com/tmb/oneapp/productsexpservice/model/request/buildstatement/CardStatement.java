package com.tmb.oneapp.productsexpservice.model.request.buildstatement;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CardStatement {
    private BigDecimal totalUnbilledAmounts;
    private BigDecimal fullPaymentAmounts;
    private BigDecimal minPaymentAmounts;
    private BigDecimal totalAmountDue;
    private BigDecimal minimumDue;
    private BigDecimal interests;
    private BigDecimal cashAdvanceFee;
    private Integer totalCashInstallmentRecords;
    private Integer totalSpendingInstallmentRecords;
    private String dueDate;
    private String statementDate;
    private String promotionFlag;
    private BigDecimal pointEarned;
    private BigDecimal pointAvailable;
    private BigDecimal pointRemain;
    private BigDecimal expiryPoints;
    private String expiryDate;
    private List<StatementTransaction> statementTransactions;
}
