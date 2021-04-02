package com.tmb.oneapp.productsexpservice.model.request.fundlist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundListRq {

    private String crmId;
    @NotNull
    private List<String> unitHolderNo;
}
