package com.tmb.oneapp.productsexpservice.model.response.statustracking;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApplicationStatusApplication {
    private String status;
    private String productCode;
    private String productCategoryTh;
    private String productCategoryEn;
    private String productTypeTh;
    private String productTypeEn;
    private String productDetailTh;
    private String productDetailEn;
    private String referenceNo;
    private String imageUrl;
    private int currentNode;
    private List<String> nodeTextTh;
    private List<String> nodeTextEn;
    private String topRemarkTh;
    private String topRemarkEn;
    private String bottomRemarkTh;
    private String bottomRemarkEn;
    private String applicationDate;
    private String lastUpdateDate;
    private Boolean isApproved;
    private Boolean isRejected;

}
