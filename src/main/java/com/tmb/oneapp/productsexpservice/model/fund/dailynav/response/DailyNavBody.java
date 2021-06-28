package com.tmb.oneapp.productsexpservice.model.fund.dailynav.response;

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
public class DailyNavBody {

    @JsonProperty(value = "fundList")
    private List<Fund> funds;
}
