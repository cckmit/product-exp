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
public class CustomerCaseSubmitBody {
    @ApiModelProperty(notes = "Firstname", example="NAME")
    private String firstname;
    @ApiModelProperty(notes = "Lastname", example="TEST")
    private String lastname;
    @ApiModelProperty(notes = "ServiceTypeMatrixCode", example="O0001")
    private String serviceTypeMatrixCode;
    @ApiModelProperty(notes = "Note", example="awefwefwaefwef")
    private String note;
}
