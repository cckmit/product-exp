package com.tmb.oneapp.productsexpservice.model.activitylog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.common.model.BaseEvent;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityLogs extends BaseEvent {

    @JsonProperty("activity_type")
    private String activityType;

    private String reason;

    @JsonProperty("verify_flag")
    private String verifyFlag;

    @JsonProperty("fund_name")
    private String fundCode;

    @JsonProperty("unit_holder")
    private String unitHolderNo;

    @JsonProperty("fund_class")
    private String fundClass;

    public ActivityLogs(String correlationId, String activityDate, String activityTypeId) {
        super(correlationId, activityDate, activityTypeId);
    }
}