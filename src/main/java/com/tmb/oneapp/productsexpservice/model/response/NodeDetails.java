package com.tmb.oneapp.productsexpservice.model.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NodeDetails {
    private String loanSystem;
    private String loanType;
    private String loanDesc;
    private String productCode;
    private int totalNode;
    private List<String> nodeTh;
    private List<String> nodeEn;

}
