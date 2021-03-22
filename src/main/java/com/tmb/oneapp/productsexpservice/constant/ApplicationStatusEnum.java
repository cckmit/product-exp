package com.tmb.oneapp.productsexpservice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

/**
 * enum class for response code to maintain response status
 */
@Getter
@AllArgsConstructor
public enum ApplicationStatusEnum implements Serializable {
    PRE("PRE", APPLICATION_STATUS_IN_PROGRESS, APPLICATION_STATUS_IN_PROGRESS, 1),
    CK("CK", APPLICATION_STATUS_IN_PROGRESS, APPLICATION_STATUS_IN_PROGRESS, 2),
    AP("AP", APPLICATION_STATUS_IN_PROGRESS, APPLICATION_STATUS_IN_PROGRESS, 3),
    SD2("SD2", APPLICATION_STATUS_COMPLETED, APPLICATION_STATUS_COMPLETED, 4),
    CC3N("CC3+N", APPLICATION_STATUS_REJECTED, APPLICATION_STATUS_COMPLETED, 2),
    CC1("CC1", APPLICATION_STATUS_REJECTED, APPLICATION_STATUS_COMPLETED, 2),
    CC4("CC4", APPLICATION_STATUS_REJECTED, APPLICATION_STATUS_COMPLETED, 2);

    private final String hpStatus;
    private final String status;
    private final String statusGroup;
    private final int currentNode;

}