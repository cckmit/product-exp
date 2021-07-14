package com.tmb.oneapp.productsexpservice.model.response.fundfavorite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerFavoriteFundData {

    private String id;

    @JsonProperty(value = "custId")
    private String customerId;

    private String fundCode;

    private Date createDate;

    private Date updateDate;

    private String isFavorite;
}
