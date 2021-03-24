package com.tmb.oneapp.productsexpservice.model.response.statustracking;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LendingRslStatusResponse {
    private String status;
    private String appType;
    private String productCode;
    private String productTypeTh;
    private String productTypeEn;
    private String referenceNo;
    private String currentNode;
    private List<String> nodeTextTh;
    private List<String> nodeTextEn;
    private String applicationDate;
    private String lastUpdateDate;
    private String isApproved;
    private String isRejected;
    private String imageUrl;
    private String topRemarkEn;
    private String topRemarkTh;
    private String bottomRemarkTh;
    private String bottomRemarkEn;

}
