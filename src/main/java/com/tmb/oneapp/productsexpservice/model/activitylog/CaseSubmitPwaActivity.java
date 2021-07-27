package com.tmb.oneapp.productsexpservice.model.activitylog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tmb.common.model.BaseEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CaseSubmitPwaActivity extends BaseEvent {
    public CaseSubmitPwaActivity(String correlationId, String activityDate, String activityTypeId) {
        super(correlationId, activityDate, activityTypeId);
    }

    @JsonProperty("case_number")
    private String caseNumber;

    @JsonProperty("request_date")
    private String requestDate;
}
