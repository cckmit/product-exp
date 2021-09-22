package com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request;

import com.tmb.oneapp.productsexpservice.model.productexperience.mutualfund.HeaderRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellAndSwitchRequest {

    private HeaderRequest header;

    private SellAndSwitchRequestBody body;

}
