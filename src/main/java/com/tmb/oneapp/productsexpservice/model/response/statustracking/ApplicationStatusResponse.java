package com.tmb.oneapp.productsexpservice.model.response.statustracking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApplicationStatusResponse {
    private Boolean firstUsageExperience;
    private String serviceTypeId;
    private List<ApplicationStatusApplication> inProgress;
    private List<ApplicationStatusApplication> completed;
    @JsonIgnore
    private int hpStatus;
    @JsonIgnore
    private int rslStatus;

}
