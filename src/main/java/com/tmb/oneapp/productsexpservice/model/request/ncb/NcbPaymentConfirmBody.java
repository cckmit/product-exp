package com.tmb.oneapp.productsexpservice.model.request.ncb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NcbPaymentConfirmBody {
    @ApiModelProperty(notes = "Service Type Id", required=true, example="NCBR")
    private String serviceTypeId;
    @ApiModelProperty(notes = "FirstnameTh", required=true, example="NAME")
    private String firstnameTh;
    @ApiModelProperty(notes = "LastnameTh", required=true, example="TEST")
    private String lastnameTh;
    @ApiModelProperty(notes = "FirstnameEn", required=true, example="NAME")
    private String firstnameEn;
    @ApiModelProperty(notes = "LastnameEn", required=true, example="TEST")
    private String lastnameEn;
    @ApiModelProperty(notes = "Email", required=true, example="abc@tmb.com")
    private String email;
    @ApiModelProperty(notes = "Address", required=true, example="123/12 abcesafaefae")
    private String address;
    @ApiModelProperty(notes = "Delivery Method", required=true, example="by email")
    private String deliveryMethod;
    @ApiModelProperty(notes = "Account Number", required=true, example="1234567890")
    private String accountNumber;
}
