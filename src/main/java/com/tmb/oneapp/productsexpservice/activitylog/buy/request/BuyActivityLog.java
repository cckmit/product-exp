package com.tmb.oneapp.productsexpservice.activitylog.buy.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.common.model.BaseEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyActivityLog extends BaseEvent {

    @JsonProperty("unit_holder")
    private String unitHolderNumber;

    @JsonProperty("fund_name")
    private String fundName;

    @JsonProperty("verify_flag")
    private String verifyFlag;

    private String reason;

    @JsonProperty("fund_class")
    private String fundClass;

    @JsonProperty("activity_type")
    private String activityType;

    public BuyActivityLog(String correlationId, String activityDate, String activityTypeId) {
        super(correlationId, activityDate, activityTypeId);
    }
}
