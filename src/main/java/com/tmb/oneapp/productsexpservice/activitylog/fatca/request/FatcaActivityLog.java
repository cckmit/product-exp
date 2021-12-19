package com.tmb.oneapp.productsexpservice.activitylog.fatca.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tmb.common.model.BaseEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FatcaActivityLog extends BaseEvent {

    @JsonProperty("complete_fatca_form")
    private String completeFatcaForm;

    @JsonProperty("fatca_flag")
    private String fatcaFlag;

    public FatcaActivityLog(String correlationId, String activityDate, String activityTypeId) {
        super(correlationId, activityDate, activityTypeId);
    }
}
