package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tmb.common.model.legacy.rsl.common.ob.apprmemo.creditcard.ApprovalMemoCreditCard;
import com.tmb.common.model.legacy.rsl.common.ob.apprmemo.facility.ApprovalMemoFacility;
import com.tmb.oneapp.productsexpservice.model.request.loan.ProductRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InstantLoanCalUWResponse {
    private String status;
    private ApprovalMemoCreditCard[] approvalMemoCreditCards;
    private ApprovalMemoFacility[] approvalMemoFacilities;
    private ProductRequest productRequest;
}
