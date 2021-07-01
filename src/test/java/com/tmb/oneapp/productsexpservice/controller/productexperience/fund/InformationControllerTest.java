package com.tmb.oneapp.productsexpservice.controller.productexperience.fund;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.dto.fund.information.InformationDto;
import com.tmb.oneapp.productsexpservice.model.fund.dailynav.response.DailyNavResponse;
import com.tmb.oneapp.productsexpservice.model.fund.information.request.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.model.fund.information.response.InformationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.fund.InformationService;
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
class InformationControllerTest {

    @Mock
    private InformationService informationService;

    @InjectMocks
    private InformationController informationController;

    @Test
    void should_return_information_dto_when_call_get_fund_information_given_correlation_id_and_fund_code_request_body() throws IOException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FundCodeRequestBody fundCodeRequestBody = FundCodeRequestBody.builder()
                .code("TMBCOF")
                .build();

        InformationResponse informationResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/fund_information.json").toFile(),
                InformationResponse.class);
        DailyNavResponse dailyNavResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/fund_daily_nav.json").toFile(),
                DailyNavResponse.class);

        InformationDto informationDto = InformationDto.builder()
                .information(informationResponse.getData())
                .dailyNav(dailyNavResponse.getData())
                .build();
        when(informationService.getFundInformation(correlationId, fundCodeRequestBody)).thenReturn(informationDto);

        //When
        ResponseEntity<TmbOneServiceResponse<InformationDto>> actual = informationController.getFundInformation(correlationId, fundCodeRequestBody);

        //Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(informationDto, actual.getBody().getData());
    }

    @Test
    void should_return_information_dto_null_when_call_get_fund_information_given_information_dto_empty_from_service() {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FundCodeRequestBody fundCodeRequestBody = FundCodeRequestBody.builder()
                .code("TMBCOF")
                .build();
        when(informationService.getFundInformation(correlationId, fundCodeRequestBody)).thenReturn(null);

        //When
        ResponseEntity<TmbOneServiceResponse<InformationDto>> actual = informationController.getFundInformation(correlationId, fundCodeRequestBody);

        //Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertNull(actual.getBody().getData());
    }

    @Test
    void should_return_information_dto_null_when_call_get_fund_information_given_throw_exception_from_service() {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FundCodeRequestBody fundCodeRequestBody = FundCodeRequestBody.builder()
                .code("TMBCOF")
                .build();
        when(informationService.getFundInformation(correlationId, fundCodeRequestBody)).thenThrow(RuntimeException.class);

        //When
        ResponseEntity<TmbOneServiceResponse<InformationDto>> actual = informationController.getFundInformation(correlationId, fundCodeRequestBody);

        //Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertNull(actual.getBody().getData());
    }
}