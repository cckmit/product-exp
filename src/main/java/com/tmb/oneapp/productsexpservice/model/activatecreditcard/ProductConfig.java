package com.tmb.oneapp.productsexpservice.model.activatecreditcard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProductConfig {
    private String id;
    private String productCode;
    private String rslProductCode;
    private String productDescription;
    private String productNameEN;
    private String productNameTH;
    private String accountType;
    private String iconId;
    private String sortOrder;
    private Integer productType;
    private Integer accountSummaryDisplay;
    private Integer accountDetailView;
    private Integer allowTransferFromAccount;
    private String transferOwnTTBMapCode;
    private String transferOtherTTBMapCode;
    private Integer allowTransferToOtherBank;
    private Integer allowRegisterPromptPay;
    private Integer allowTransferToPromptPay;
    private Integer allowCardLessWithdraw;
    private Integer allowManageDebitCard;
    private Integer allowPointRedemption;
    private Integer allowCassBack;
    private Integer allowCashAdvance;
    private Integer allowSogoood;
    private Integer allowCashtransfer;
    private Integer allowBuy;
    private Integer allowSell;
    private Integer allowHistory;
    private Integer waiveFeeForPromptPay;
    private Integer waiveFeeForPromptPayAccount;
    private Integer transferShortcutFlag;
    private Integer payBillShortcutFlag;
    private Integer topUpShortcutFlag;
    private Integer payMyCcShortcutFlag;
    private Integer payMyLoanShortcutFlag;
}
