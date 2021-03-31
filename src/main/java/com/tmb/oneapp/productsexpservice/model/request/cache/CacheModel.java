package com.tmb.oneapp.productsexpservice.model.request.cache;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CacheModel {
    private String key;
    private long ttl;
    private String value;
}
