package com.tmb.oneapp.productsexpservice.model.lending.document;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class SubmitDocumentRequest {

    @NotEmpty
    private String caId;

}
