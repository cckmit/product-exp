package com.tmb.oneapp.productsexpservice.model.lending.document;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UploadDocumentResponse {

    private List<Document> documents;
    private String appRefNo;
    private String appType;
    private String productDescTh;

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Document {
        private String docCode;
        private String pdfFileName;
        private String status;
    }

}
