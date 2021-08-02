package com.tmb.oneapp.productsexpservice.model.request.fundlist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundListRequest {

    @NotNull
    @JsonProperty(value = "unitHolderNo")
    private List<String> unitHolderNumber;
}
