package com.tmb.oneapp.productsexpservice.model.productexperience.fund.countprocessorder.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountOrderProcessingResponseBody {
    @JsonAlias({"countAll"})
    private String countProcessOrder;
}
