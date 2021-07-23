package com.tmb.oneapp.productsexpservice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * enum class for response code to maintain response status
 */
@Getter
@AllArgsConstructor
public enum ApplicationStatusEnum implements Serializable {
    PRE("PRE", ProductsExpServiceConstant.APPLICATION_STATUS_IN_PROGRESS, ProductsExpServiceConstant.APPLICATION_STATUS_IN_PROGRESS, 1),
    CK("CK", ProductsExpServiceConstant.APPLICATION_STATUS_IN_PROGRESS, ProductsExpServiceConstant.APPLICATION_STATUS_IN_PROGRESS, 2),
    AP("AP", ProductsExpServiceConstant.APPLICATION_STATUS_IN_PROGRESS, ProductsExpServiceConstant.APPLICATION_STATUS_IN_PROGRESS, 3),
    SD2("SD2", ProductsExpServiceConstant.APPLICATION_STATUS_COMPLETED, ProductsExpServiceConstant.APPLICATION_STATUS_COMPLETED, 4),
    CC("CC", ProductsExpServiceConstant.APPLICATION_STATUS_REJECTED, ProductsExpServiceConstant.APPLICATION_STATUS_COMPLETED, 2);

    private final String hpStatus;
    private final String status;
    private final String statusGroup;
    private final int currentNode;

}