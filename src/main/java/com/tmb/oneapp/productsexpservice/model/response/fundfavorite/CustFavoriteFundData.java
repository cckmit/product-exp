package com.tmb.oneapp.productsexpservice.model.response.fundfavorite;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustFavoriteFundData {
    private String id;
    private String custId;
    private String fundCode;
    private Date createDate;
    private Date updateDate;
    private String isFavorite;
}
