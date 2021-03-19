package com.tmb.oneapp.productsexpservice.model.cardinstallment;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InstallmentPlan {

    private String planSeqId;

    private String installmentsPlan;


    private String planDesc;


    private String paymentTerm;


    private String interestRate;


    private String merchantNo;


    private String channel;


    private String planStatus;
}
