package com.tmb.oneapp.productsexpservice.model.response.stmtresponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatementList{
    private String seqNo;
    private String portfolioNumber;
    private String orderID;
    private String orderDate;
    private String fundCode;
    private String fundShortName;
    private String fundType;
    private String cancellable;
    private String effectiveDate;
    private String amount;
    private String price;
    private String tranTypeGroup;
    private String unit;
    private String tranTypeEN;
    private String tranTypeTH;
    private String statusEN;
    private String statusTH;
    private String channelEN;
    private String channelTH;
    private String paymentDate;
}
