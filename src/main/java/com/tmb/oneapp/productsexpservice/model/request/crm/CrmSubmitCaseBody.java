package com.tmb.oneapp.productsexpservice.model.request.crm;

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
public class CrmSubmitCaseBody {
    @ApiModelProperty(notes = "FirstnameTh", example="NAME")
    private String firstnameTh;
    @ApiModelProperty(notes = "LastnameTh", example="TEST")
    private String lastnameTh;
    @ApiModelProperty(notes = "FirstnameEn", example="NAME")
    private String firstnameEn;
    @ApiModelProperty(notes = "LastnameEn", example="TEST")
    private String lastnameEn;
    @ApiModelProperty(notes = "ServiceTypeMatrixCode", example="O0004")
    private String serviceTypeMatrixCode;
    @ApiModelProperty(notes = "Note", example="awefwefwaefwef")
    private String note;
}
