package com.tmb.oneapp.productsexpservice.model.lending.document;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UploadDocumentRequest {

    @NotNull
    private long caId;
    @NotEmpty
    private String docCode;
    @NotEmpty
    private String file;

}
