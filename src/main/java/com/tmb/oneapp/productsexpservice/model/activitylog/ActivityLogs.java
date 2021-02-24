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
    public ActivityLogs(String correlationId, String activityDate, String activityTypeId) {
        super(correlationId, activityDate, activityTypeId);
    }
    @JsonProperty("activity_type")
    private String activityType;
    private String reason;
    @JsonProperty("verify_flag")
    private String verifyFlag;
    private String fundCode;
    private String unitHolderNo;

}
