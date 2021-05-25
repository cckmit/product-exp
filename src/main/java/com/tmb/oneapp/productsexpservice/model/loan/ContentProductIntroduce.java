package com.tmb.oneapp.productsexpservice.model.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ContentProductIntroduce implements Serializable {
    private ContentDetailProductIntroduce en;
    private ContentDetailProductIntroduce th;
    private String iconUrlIos;
    private String iconUrlAndroid;
}
