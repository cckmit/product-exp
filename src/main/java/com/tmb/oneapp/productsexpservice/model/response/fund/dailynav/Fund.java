package com.tmb.oneapp.productsexpservice.model.response.fund.dailynav;

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

    private String code;

    private String shortName;

    @JsonProperty(value = "dailyNavList")
    private List<List<String>> dailyNavDto;
}
