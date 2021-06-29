package com.tmb.oneapp.productsexpservice.model.request.crm;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CrmSearchBody {
    @ApiModelProperty(notes = "search_type", example="rm-id")
    private String searchType;
    @ApiModelProperty(notes = "search_value", example="TEST")
    private String searchValue;
}
