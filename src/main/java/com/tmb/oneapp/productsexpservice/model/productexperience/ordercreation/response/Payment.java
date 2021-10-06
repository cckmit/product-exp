package com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response;

import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Account;
import lombok.Data;

@Data
public class Payment {
    private String requestId;
    private String requestDateTime;
    private String appId;
    private String paymentChannel;
    private String paymentId;
    private String tellerId;
    private String epayCode;
    private Account fromAccount;
    private Account toAccount;

}
