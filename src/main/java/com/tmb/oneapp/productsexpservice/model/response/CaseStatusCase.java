package com.tmb.oneapp.productsexpservice.model.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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

}
