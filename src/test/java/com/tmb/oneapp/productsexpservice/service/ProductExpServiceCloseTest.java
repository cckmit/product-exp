package com.tmb.oneapp.productsexpservice.service;

import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundAccountResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class ProductExpServiceCloseTest {

    @Test
    public void validateTMBResponse() {
        FundAccountResponse fundAccountResponse = UtilMap.validateTMBResponse(null, null, null);
        Assert.assertNull(fundAccountResponse);
    }

    @Test
    public void mappingPaymentResponse() {
        UtilMap utilMap = new UtilMap();
        FundPaymentDetailResponse fundAccountRs = utilMap.mappingPaymentResponse(null, null, null, null, null);
        Assert.assertNull(fundAccountRs);
    }

    @Test
    public void convertAccountType() {
        String fundAccountRs = UtilMap.convertAccountType("AAAA");
        Assert.assertEquals("", fundAccountRs);
    }

    @Test
    public void isCASADormantException() {
        boolean fundAccountRs = UtilMap.isCASADormant("data not found");
        Assert.assertFalse(fundAccountRs);
    }

    @Test
    public void isCASADormant() {
        boolean fundAccountRs = UtilMap.isCASADormant(null);
        Assert.assertTrue(fundAccountRs);
    }

    @Test
    public void isBusinessCloseException() {
        boolean fundAccountRs = UtilMap.isBusinessClose("10:00", "10:01");
        Assert.assertFalse(fundAccountRs);
    }

    @Test
    public void addColonDateFormat() {
        String fundAccountRs = UtilMap.deleteColonDateFormat("06:00");
        Assert.assertEquals("0600", fundAccountRs);
    }

    @Test
    public void addColonDateFormatFail() {
        String fundAccountRs = UtilMap.deleteColonDateFormat("");
        Assert.assertEquals("", fundAccountRs);
    }
}
