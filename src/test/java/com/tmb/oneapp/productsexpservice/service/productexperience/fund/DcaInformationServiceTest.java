package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.information.DcaInformationDto;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.information.DcaInformationModel;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.mapper.dcainformation.DcaInformationMapper;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListBody;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DcaInformationServiceTest {

    @InjectMocks
    public DcaInformationService dcaInformationService;

    @Mock
    private TMBLogger<DcaInformationServiceTest> logger;

    @Mock
    public CustomerExpServiceClient customerExpServiceClient;

    @Mock
    public InvestmentRequestClient investmentRequestClient;

    @Mock
    public ProductsExpService productsExpService;

    @Mock
    public DcaInformationMapper dcaInformationMapper;

    @Test
    void should_return_dca_information_dto_when_call_get_dca_information_given_correlation_id_and_crm_id() throws IOException, TMBCommonException {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";

        when(productsExpService.getPortList(any(), anyString(), anyBoolean())).thenReturn(List.of("222222"));
        TmbStatus successStatus = TmbStatusUtil.successStatus();
        TmbOneServiceResponse<FundSummaryBody> tmbFundSummaryResponse = new TmbOneServiceResponse<>();
        FundSummaryBody fundSummaryBody = mapper.readValue(Paths.get("src/test/resources/investment/fund/dca/fundsummarybody.json").toFile(), FundSummaryBody.class);
        tmbFundSummaryResponse.setData(fundSummaryBody);
        tmbFundSummaryResponse.setStatus(successStatus);
        when(investmentRequestClient.callInvestmentFundSummaryService(any(),
                any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbFundSummaryResponse));

        TmbOneServiceResponse<FundListBody> tmbFundListResponse = new TmbOneServiceResponse<>();
        FundListBody fundListBody = mapper.readValue(Paths.get("src/test/resources/investment/fund/dca/fundlistinfo.json").toFile(), FundListBody.class);
        tmbFundListResponse.setData(fundListBody);
        tmbFundListResponse.setStatus(successStatus);
        when(investmentRequestClient.callInvestmentFundListInfoService(any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbFundListResponse));

        DcaInformationModel dcaInformationModel = mapper.readValue(Paths.get("src/test/resources/investment/fund/dca/dcainformationmodel.json").toFile(), DcaInformationModel.class);
        when(dcaInformationMapper.fundClassInfoToDcaInformationModel(any())).thenReturn(dcaInformationModel);

        // When
        TmbOneServiceResponse<DcaInformationDto> actual = dcaInformationService.getDcaInformation(correlationId, crmId);

        // Then
        DcaInformationDto dcaInformationDto = mapper.readValue(Paths.get("src/test/resources/investment/fund/dca/dcainformationdto.json").toFile(), DcaInformationDto.class);
        assertEquals(TmbStatusUtil.successStatus().getCode(), actual.getStatus().getCode());
        assertEquals(dcaInformationDto, actual.getData());
    }

    @Test
    void should_return_null_when_call_get_dca_information_given_correlation_id_and_crm_id() throws JsonProcessingException, TMBCommonException {
        // Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";

        when(productsExpService.getPortList(any(), anyString(), anyBoolean()))
                .thenThrow(new RuntimeException("Error"));
        // When
        TmbOneServiceResponse<DcaInformationDto> actual = dcaInformationService.getDcaInformation(correlationId, crmId);

        // Then
        assertNull(actual.getStatus());
        assertNull(actual.getData());
    }

    @Test
    void should_throw_tmb_common_exception_when_call_get_dca_information_given_correlation_id_and_crm_id() throws IOException, TMBCommonException {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";

        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        TmbOneServiceResponse<FundSummaryBody> tmbFundSummaryResponse = new TmbOneServiceResponse<>();
        tmbFundSummaryResponse.setStatus(getMockBadRequest(errorCode,errorMessage));
        when(investmentRequestClient.callInvestmentFundSummaryService(any(),
                any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbFundSummaryResponse));
        // When
        try {
            dcaInformationService.getDcaInformation(correlationId, crmId);
        }catch (TMBCommonException ex){

            // Then
            assertEquals(errorCode,ex.getErrorCode());
            assertEquals(errorMessage,ex.getErrorMessage());
        }
    }

    private TmbStatus getMockBadRequest(String errorCode,String errorMessage){
        return new TmbStatus(errorCode,errorMessage,"investment-service",errorMessage);
    }

}
