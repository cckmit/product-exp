package com.tmb.oneapp.productsexpservice.model.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CaseStatusResponse {
    private String serviceTypeId;
    private Boolean firstUsageExperience;
    private List<CaseStatusCase> inProgress;
    private List<CaseStatusCase> completed;

}
