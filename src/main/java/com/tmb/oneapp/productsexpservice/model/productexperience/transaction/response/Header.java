package com.tmb.oneapp.productsexpservice.model.productexperience.transaction.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Header {

    @JsonProperty(value = "rqUID")
    private String requestUid;

    private String channelID;

    private String serviceName;

    @JsonProperty(value = "rqDateTime")
    private String requestDateTime;

    @JsonProperty(value = "rsDateTime")
    private String responseDateTime;

    private Status status;
}
