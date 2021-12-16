package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.model.BaseEvent;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.activitylog.service.LogActivityService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

@RunWith(JUnit4.class)
public class LogActivityServiceTest {

    @Mock
    private KafkaProducerService kafkaProducerService;

    private LogActivityService logActivityService;

    private final String crmId = "001100000000000000000001184383";

    private final String ipAddress = "0.0.0.0";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        logActivityService = new LogActivityService(kafkaProducerService);
    }

    @Test
    void should_call_create_log_once_when_call_create_log_given_base_event() {
        // Given
        // When
        BaseEvent baseEvent = new BaseEvent();
        logActivityService.createLog(baseEvent);

        // Then
        assertTrue(true);
    }

    @Test
    void should_return_base_event_success_only_when_call_build_common_data_given_crm_id_and_ip_address() {
        // Given
        // When
        BaseEvent actual = logActivityService.buildCommonData(crmId, ipAddress);

        // Then
        BaseEvent expected = new BaseEvent();
        expected.setCrmId(crmId);
        expected.setChannel("mb");
        expected.setAppVersion("1.0.0");
        expected.setIpAddress(ipAddress);
        expected.setActivityStatus("Success");

        assertAll("Should return base event data",
                () -> assertEquals(expected.getCrmId(), actual.getCrmId()),
                () -> assertEquals(expected.getChannel(), actual.getChannel()),
                () -> assertEquals(expected.getAppVersion(), actual.getAppVersion()),
                () -> assertEquals(expected.getIpAddress(), actual.getIpAddress()),
                () -> assertEquals(expected.getActivityStatus(), actual.getActivityStatus()),
                () -> assertEquals(expected.getFailReason(), actual.getFailReason())
        );
    }

    @Test
    void should_return_base_event_success_when_call_build_common_data_given_crm_id_and_ip_address_and_creation_response() {
        // Given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse();
        TmbStatus status = new TmbStatus();
        status.setCode(ProductsExpServiceConstant.SUCCESS_CODE);
        response.setStatus(status);
        response.setData("");

        // When
        BaseEvent actual = logActivityService.buildCommonData(crmId, ipAddress, response);

        // Then
        BaseEvent expected = new BaseEvent();
        expected.setCrmId(crmId);
        expected.setChannel("mb");
        expected.setAppVersion("1.0.0");
        expected.setIpAddress(ipAddress);
        expected.setActivityStatus("Success");

        assertAll("Should return base event data",
                () -> assertEquals(expected.getCrmId(), actual.getCrmId()),
                () -> assertEquals(expected.getChannel(), actual.getChannel()),
                () -> assertEquals(expected.getAppVersion(), actual.getAppVersion()),
                () -> assertEquals(expected.getIpAddress(), actual.getIpAddress()),
                () -> assertEquals(expected.getActivityStatus(), actual.getActivityStatus()),
                () -> assertEquals(expected.getFailReason(), actual.getFailReason())
        );
    }

    @Test
    void should_return_base_event_failure_when_call_build_common_data_given_crm_id_and_ip_address_and_creation_response() {
        // Given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse();
        TmbStatus status = new TmbStatus();
        status.setDescription(ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE);
        status.setCode(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE);
        response.setStatus(status);
        response.setData("");

        // When
        BaseEvent actual = logActivityService.buildCommonData(crmId, ipAddress, response);

        // Then
        BaseEvent expected = new BaseEvent();
        expected.setCrmId(crmId);
        expected.setChannel("mb");
        expected.setAppVersion("1.0.0");
        expected.setIpAddress(ipAddress);
        expected.setActivityStatus("Failure");
        expected.setFailReason("DATA NOT FOUND");

        assertAll("Should return base event data",
                () -> assertEquals(expected.getCrmId(), actual.getCrmId()),
                () -> assertEquals(expected.getChannel(), actual.getChannel()),
                () -> assertEquals(expected.getAppVersion(), actual.getAppVersion()),
                () -> assertEquals(expected.getIpAddress(), actual.getIpAddress()),
                () -> assertEquals(expected.getActivityStatus(), actual.getActivityStatus()),
                () -> assertEquals(expected.getFailReason(), actual.getFailReason())
        );
    }
}
