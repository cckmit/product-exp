package com.tmb.oneapp.productsexpservice.model.productexperience.fund.dailynav.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fund {

    @JsonProperty(value = "fundCode")
    private String code;

    @JsonProperty(value = "fundShortName")
    private String shortName;

    @JsonProperty(value = "dailyNavList")
    private List<List<String>> dailyNavDto;

    @JsonProperty(value="durationPills")
    private List<String> durationPills;
}
