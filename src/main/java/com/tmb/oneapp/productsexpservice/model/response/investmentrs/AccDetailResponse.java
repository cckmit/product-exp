package com.tmb.oneapp.productsexpservice.model.response.investmentrs;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccDetailResponse {
    private Header header;
    private AccDetailBody body;
}
