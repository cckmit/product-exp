package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class InstallmentData {

    @JsonProperty("productGroup")
    private String productGroup;
    @JsonProperty("segment")
    private String segment;
    @JsonProperty("cashChillChillModel")
    private String cashChillChillModel;
    @JsonProperty("cashTransferModel")
    private String cashTransferModel;
    @JsonProperty("modelTenors")
    private List<ModelTenor> modelTenors;
}
