package com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour;

import com.tmb.common.model.TmbStatus;
import lombok.Data;

@Data
public class TmbStatusWithTime extends TmbStatus {

    private String startTime;

    private String endTime;

    public TmbStatusWithTime(String code, String message, String service) {
        super(code, message, service);
    }

    public TmbStatusWithTime(String code, String message, String service, String description) {
        super(code, message, service, description);
    }

    public TmbStatusWithTime() {
    }
}
