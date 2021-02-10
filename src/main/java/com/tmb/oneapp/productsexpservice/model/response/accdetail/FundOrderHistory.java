package com.tmb.oneapp.productsexpservice.model.response.accdetail;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundOrderHistory {
    private String orderDateTemp;
    private String itemNo;
    private String orderReference;
    private String orderDate;
    private String efftDate;
    private String amount;
    private String unit;
    private String tranTypeHubEN;
    private String tranTypeHubTH;
    private String statusHubEN;
    private String statusHubTH;
    private String channelHubEN;
    private String channelHubTH;
}
