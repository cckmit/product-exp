package com.tmb.oneapp.productsexpservice.model.response.fundfactsheet;


import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundResponse {

    private boolean isError;

    private String errorCode;

    private String errorMsg;

    private String errorDesc;
}
