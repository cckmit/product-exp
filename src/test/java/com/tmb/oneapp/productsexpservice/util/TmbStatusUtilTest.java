package com.tmb.oneapp.productsexpservice.util;

import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TmbStatusUtilTest {

    @Test
    void should_return_success_status_when_call_success_status() {
        // Given
        // When
        TmbStatus actual = TmbStatusUtil.successStatus();

        // Then
        TmbStatus expected = new TmbStatus();
        expected.setCode(ProductsExpServiceConstant.SUCCESS_CODE);
        expected.setDescription(ProductsExpServiceConstant.SUCCESS_MESSAGE);
        expected.setMessage(ProductsExpServiceConstant.SUCCESS_MESSAGE);
        expected.setService(ProductsExpServiceConstant.SERVICE_NAME);

        assertAll("should return success status",
                () -> assertEquals(expected.getCode(), actual.getCode()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getMessage(), actual.getMessage()),
                () -> assertEquals(expected.getService(), actual.getService())
        );
    }

    @Test
    void should_return_not_found_status_when_call_not_found_status() {
        // Given
        // When
        TmbStatus actual = TmbStatusUtil.notFoundStatus();

        // Then
        TmbStatus expected = new TmbStatus();
        expected.setCode(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE);
        expected.setDescription(ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE);
        expected.setMessage(ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE);
        expected.setService(ProductsExpServiceConstant.SERVICE_NAME);

        assertAll("should return success status",
                () -> assertEquals(expected.getCode(), actual.getCode()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getMessage(), actual.getMessage()),
                () -> assertEquals(expected.getService(), actual.getService())
        );
    }
}