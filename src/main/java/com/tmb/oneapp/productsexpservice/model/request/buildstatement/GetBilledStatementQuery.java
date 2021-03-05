package com.tmb.oneapp.productsexpservice.model.request.buildstatement;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetBilledStatementQuery {
    @ApiModelProperty(notes = "accountId", required=true, example="0000000050078680472000929")
    private String accountId;
    @ApiModelProperty(notes = "periodStatement", example="1")
    private String periodStatement;
    @ApiModelProperty(notes = "moreRecords", example="Y")
    private String moreRecords;
    @ApiModelProperty(notes = "searchKeys", example=" ")
    private String searchKeys;
}
