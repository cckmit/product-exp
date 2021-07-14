package com.tmb.oneapp.productsexpservice.model.fund.dcainformation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DcaInformationRequestBody {
    @NotNull
    @JsonProperty(value = "rmId")
    private String crmId;
}
