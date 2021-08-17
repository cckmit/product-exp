package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tmb.common.model.creditcard.CardInstallment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
@ToString
public class CardInstallmentQuery {
    @ApiModelProperty(notes = "accountId", required = true, example = "0000000000012017890000383")
    @JsonProperty("account_id")
    private String accountId;
    @JsonProperty("card_installment")
    private List<CardInstallment> cardInstallment;

}
