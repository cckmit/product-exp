package com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.common.model.BaseEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SellActivityLog extends BaseEvent {

    /* Click Redeem Button */
    @JsonProperty("unit_holder")
    private String unitHolder;

    @JsonProperty("fund_name")
    private String fundName;

    @JsonProperty("fund_class")
    private String fundClass;

    @JsonProperty("type_of_selling")
    private String typeOfSelling;

    @JsonProperty("amount")
    private String amount;

    /* Enter Pin Is Correct */
    private String status;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("receiving_account")
    private String receivingAccount;

    @JsonProperty("activity_type")
    private String activityType;

    public SellActivityLog(String correlationId, String activityDate, String activityTypeId) {
        super(correlationId, activityDate, activityTypeId);
    }
}
