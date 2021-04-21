package com.tmb.oneapp.productsexpservice.model.response.statustracking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseStatusCase {
    private String status;
    private String productNameTh;
    private String productNameEn;
    private String issueTh;
    private String issueEn;
    private String referenceNo;
    private String issueDate;
    private String expectedFinishDate;
    private String finishDate;
    private String serviceTypeMatrixCode;

}
