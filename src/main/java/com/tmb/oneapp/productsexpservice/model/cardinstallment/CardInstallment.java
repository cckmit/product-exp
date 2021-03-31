package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CardInstallment {
    @ApiModelProperty(notes = "modelType", required=true, example="IP")
    @JsonProperty("model_type")
    private String modelType;
    @ApiModelProperty(notes = "amounts", required=true, example="5555.77")
    private String amounts;
    @ApiModelProperty(notes = "transactionKey", required=true, example="T0000018980000012")
    @JsonProperty("transaction_key")
    private String transactionKey;
    @ApiModelProperty(notes = "promotionModelNo", required=true, example="IPP001")
    @JsonProperty("promotion_model_no")
    private String promotionModelNo;
    @JsonProperty("monthly_installments")
    private String monthlyInstallments;
    @JsonProperty("interest")
    private String interest;
}