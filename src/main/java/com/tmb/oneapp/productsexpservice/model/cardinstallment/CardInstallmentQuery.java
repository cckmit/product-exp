package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CardInstallmentQuery {
    @ApiModelProperty(notes = "accountId", required = true, example = "0000000000012017890000383")
    @JsonProperty("account_id")
    private String accountId;
    @JsonProperty("card_installment")
    private CardInstallment cardInstallment;

}
