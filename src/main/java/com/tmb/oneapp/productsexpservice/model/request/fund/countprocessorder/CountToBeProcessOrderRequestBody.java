package com.tmb.oneapp.productsexpservice.model.request.fund.countprocessorder;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountToBeProcessOrderRequestBody {
    private @NotBlank String serviceType;
    @JsonAlias({"crmId","rm"})
    private String rm;
}
