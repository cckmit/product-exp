package com.tmb.oneapp.productsexpservice.util;

import com.tmb.common.model.request.notification.NotifyCommon;
import com.tmb.common.util.NotificationUtil;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NotificationUtilTest {

    @Test
    public void testGenerateNotifyCommon() {
        NotifyCommon result = NotificationUtil.generateNotifyCommon("xCorrelationId", "channelNameEn", "channelNameTh", "productNameEN", "productNameTH", "custFullNameEn", "custFullNameTH");
        NotifyCommon expected = new NotifyCommon();
        expected.setChannelNameEn("1234");
        expected.setChannelNameTh("ะ้ดก้ดเกด");
        expected.setXCorrelationId("c83936c284cb398fA46CF16F399C");
        Assert.assertNotEquals(expected, result);
    }
}
