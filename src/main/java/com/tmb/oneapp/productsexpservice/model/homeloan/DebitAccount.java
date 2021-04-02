package com.tmb.oneapp.productsexpservice.model.homeloan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;


@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "auto_debit_method",
        "auto_debit_date"
})
@Data
public class DebitAccount {

    @JsonProperty("id")
    private String id;
    @JsonProperty("auto_debit_method")
    private String autoDebitMethod;
    @JsonProperty("auto_debit_date")
    private String autoDebitDate;

}
