package com.tmb.oneapp.productsexpservice.controller.productexperience.fund;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.information.DcaInformationDto;
import com.tmb.oneapp.productsexpservice.service.productexperience.fund.DcaInformationService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DcaInformationControllerTest {

    @InjectMocks
    public DcaInformationController dcaInformationController;

    @Mock
    public DcaInformationService dcaInformationService;

    @Test
    void should_return_dca_information_dto_when_call_get_dca_information_given_correlation_id_and_crm_id() throws IOException, TMBCommonException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";

        DcaInformationDto dcaInformationDto = mapper.readValue(Paths.get("src/test/resources/investment/fund/dca_information.json").toFile(),
                DcaInformationDto.class);
        TmbOneServiceResponse tmbOneServiceResponse = new TmbOneServiceResponse();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        tmbOneServiceResponse.setData(dcaInformationDto);

        when(dcaInformationService.getDcaInformation(correlationId, crmId)).thenReturn(tmbOneServiceResponse);

        //When
        ResponseEntity<TmbOneServiceResponse<DcaInformationDto>> actual =
                dcaInformationController.getDcaInformation(correlationId, crmId);
        //Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(dcaInformationDto, actual.getBody().getData());
    }

    @Test
    void should_return_not_found_when_call_get_dca_information_given_correlation_id_and_crm_id() throws TMBCommonException {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";

        TmbOneServiceResponse tmbOneServiceResponse = new TmbOneServiceResponse();
        tmbOneServiceResponse.setStatus(null);
        tmbOneServiceResponse.setData(null);

        when(dcaInformationService.getDcaInformation(correlationId, crmId)).thenReturn(tmbOneServiceResponse);

        //When
        ResponseEntity<TmbOneServiceResponse<DcaInformationDto>> actual =
                dcaInformationController.getDcaInformation(correlationId, crmId);
        //Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals(TmbStatusUtil.notFoundStatus().getCode(), actual.getBody().getStatus().getCode());
        assertNull(actual.getBody().getData());
    }
}
