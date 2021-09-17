package com.tmb.oneapp.productsexpservice.model.personaldetail;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChecklistResponse {
    private String checklistType;
    private String cifRelCode;
    private String status;
    private String docDescription;
    private BigDecimal docId;
    private String documentCode;
    private String incompletedDocReasonCd;
    private String incompletedDocReasonDesc;
    private BigDecimal id;
    private String isMandatory;
    private BigDecimal losCifId;
}
