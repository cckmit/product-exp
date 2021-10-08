package com.tmb.oneapp.productsexpservice.model.productexperience.mutualfund;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderRequest {

    private String serviceName;

    @JsonProperty(value = "channelID")
    private String channelId;

    private String rqDateTime;

    private String tokenKey;

    @JsonProperty(value = "rqUID")
    private String rqUid;
}
