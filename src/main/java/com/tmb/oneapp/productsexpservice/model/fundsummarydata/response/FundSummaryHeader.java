package com.tmb.oneapp.productsexpservice.model.fundsummarydata.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundSummaryHeader {
    private String rqUID;
    private String channelID;
    private String serviceName;
    private String rqDateTime;
    private String rsDateTime;
    private Status status;
}

