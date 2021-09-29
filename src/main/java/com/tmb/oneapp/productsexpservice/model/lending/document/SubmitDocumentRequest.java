package com.tmb.oneapp.productsexpservice.model.lending.document;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class SubmitDocumentRequest {

    @NotEmpty
    private String caId;
    @NotNull
    private List<String> docCodes;

}
