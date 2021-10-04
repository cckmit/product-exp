package com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.common.model.BaseEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SwitchActivityLog extends BaseEvent {

    /* Click Next Button */
    @JsonProperty("unit_holder")
    private String unitHolder;

    @JsonProperty("source_fund_name")
    private String sourceFundName;

    @JsonProperty("fund_class_of_source_fund")
    private String sourceFundClassName;

    @JsonProperty("target_fund_name")
    private String targetFundName;

    @JsonProperty("fund_class_of_target_fund")
    private String targetFundClassName;

    @JsonProperty("type_of_switching")
    private String typeOfSwitching;

    @JsonProperty("amount")
    private String amount;

    /* Enter Pin Is Correct */
    private String status;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("activity_type")
    private String activityType;

    public SwitchActivityLog(String correlationId, String activityDate, String activityTypeId) {
        super(correlationId, activityDate, activityTypeId);
    }
}
