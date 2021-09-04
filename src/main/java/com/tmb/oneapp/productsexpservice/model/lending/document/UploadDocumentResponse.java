package com.tmb.oneapp.productsexpservice.model.lending.document;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UploadDocumentResponse {

    private String pdfFileName;
    private String status;
    private String appRefNo;
    private String appType;
    private String productDescTh;

}
