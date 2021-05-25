package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ContentDetailProductIntroduce implements Serializable {
    private String labelTitle;
    private String labelDetail;
    private String buttonCta;
    private String hyperLink1Name;
    private String hyperLink1Url;
    private String hyperLink2Name;
    private String hyperLink2Url;
}
