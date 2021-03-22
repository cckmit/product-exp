package com.tmb.oneapp.productsexpservice.model.cardinstallment;

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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CampaignTransactionQuery {

    @ApiModelProperty(notes = "accountId", required = true, example = "0000000050078360018000167")
    @JsonProperty("account_id")
    private String accountId;
    @ApiModelProperty(notes = "moreRecords", required=true, example="Y")
    private String moreRecords;
    @ApiModelProperty(notes = "searchKeys", required=true, example="")
    private String searchKeys;
}
