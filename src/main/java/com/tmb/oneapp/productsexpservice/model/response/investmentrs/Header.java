package com.tmb.oneapp.productsexpservice.model.response.investmentrs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Header {
    private String channelID;
    private String rqDateTime;
    private String rqUID;
    private String rsDateTime;
    private String serviceName;
    private Status status;
}

