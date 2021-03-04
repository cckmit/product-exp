package com.tmb.oneapp.productsexpservice.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CaseStatusCase {
    private String status;
    private String productNameTh;
    private String productNameEn;
    private String issueTh;
    private String issueEn;
    private String referenceNo;
    private String issuedDate;
    private String expectedFinishDate;
    private String finishDate;

}
