package com.tmb.oneapp.productsexpservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    private String code;

    private String message;

    private String service;

    private String description;
}
