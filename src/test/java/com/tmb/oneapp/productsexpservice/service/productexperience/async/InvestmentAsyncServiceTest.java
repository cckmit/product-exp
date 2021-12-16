package com.tmb.oneapp.productsexpservice.service.productexperience.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.request.RelationshipRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.response.RelationshipResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.purpose.response.AccountPurposeResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response.AccountRedeemResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.request.OccupationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationInquiryResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationInquiryResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dailynav.response.DailyNavBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dailynav.response.DailyNavResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.request.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.response.InformationBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.response.InformationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioResponseBody;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestmentAsyncServiceTest {

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @InjectMocks
    private InvestmentAsyncService investmentAsyncService;

    @Test
    void should_return_information_body_when_call_fetch_fund_information_given_header_and_fund_code_request_body() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        InformationResponse informationResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/fund_information.json").toFile(),
                InformationResponse.class);
        TmbOneServiceResponse<InformationBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(informationResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<InformationBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);
        FundCodeRequestBody fundCodeRequestBody = FundCodeRequestBody.builder()
                .code("TMBCOF")
                .build();
        when(investmentRequestClient.getFundInformation(investmentRequestHeader, fundCodeRequestBody)).thenReturn(response);

        //When
        CompletableFuture<InformationBody> actual = investmentAsyncService.fetchFundInformation(investmentRequestHeader, fundCodeRequestBody);

        //Then
        CompletableFuture<InformationBody> expected = CompletableFuture.completedFuture(informationResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_fetch_fund_information_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.getFundInformation(any(), any())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.fetchFundInformation(any(), any());
        });

        //Then
        TMBCommonException expected = new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(),
                HttpStatus.OK,
                null);

        assertEquals(expected.getClass(), actual.getClass());
    }

    @Test
    void should_return_daily_nav_body_when_call_fetch_fund_daily_nav_given_header_and_fund_code_request_body() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        DailyNavResponse dailyNavResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/fund_daily_nav.json").toFile(),
                DailyNavResponse.class);
        TmbOneServiceResponse<DailyNavBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(dailyNavResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<DailyNavBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);
        FundCodeRequestBody fundCodeRequestBody = FundCodeRequestBody.builder()
                .code("TMBCOF")
                .build();
        when(investmentRequestClient.getFundDailyNav(investmentRequestHeader, fundCodeRequestBody)).thenReturn(response);

        //When
        CompletableFuture<DailyNavBody> actual = investmentAsyncService.fetchFundDailyNav(investmentRequestHeader, fundCodeRequestBody);

        //Then
        CompletableFuture<DailyNavBody> expected = CompletableFuture.completedFuture(dailyNavResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_fetch_fund_daily_nav_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.getFundDailyNav(any(), any())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.fetchFundDailyNav(any(), any());
        });

        //Then
        TMBCommonException expected = new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(),
                HttpStatus.OK,
                null);

        assertEquals(expected.getClass(), actual.getClass());
    }

    @Test
    void should_return_account_purpose_body_when_call_fetch_account_purpose_given_header() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        AccountPurposeResponse accountPurposeResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/account_purpose.json").toFile(),
                AccountPurposeResponse.class);
        TmbOneServiceResponse<AccountPurposeResponseBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(accountPurposeResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<AccountPurposeResponseBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);

        when(investmentRequestClient.getCustomerAccountPurpose(investmentRequestHeader)).thenReturn(response);

        //When
        CompletableFuture<AccountPurposeResponseBody> actual = investmentAsyncService.fetchAccountPurpose(investmentRequestHeader);

        //Then
        CompletableFuture<AccountPurposeResponseBody> expected = CompletableFuture.completedFuture(accountPurposeResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_fetch_account_purpose_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.getCustomerAccountPurpose(any())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.fetchAccountPurpose(any());
        });

        //Then
        TMBCommonException expected = new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(),
                HttpStatus.OK,
                null);

        assertEquals(expected.getClass(), actual.getClass());
    }

    @Test
    void should_return_occupation_inquiry_body_when_call_fetch_occupation_inquiry_given_header_and_crm_id() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        OccupationInquiryResponse occupationInquiryResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/occupation_inquiry.json").toFile(),
                OccupationInquiryResponse.class);
        TmbOneServiceResponse<OccupationInquiryResponseBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(occupationInquiryResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<OccupationInquiryResponseBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);

        when(investmentRequestClient.getCustomerOccupationInquiry(investmentRequestHeader, "00000007924129")).thenReturn(response);

        //When
        CompletableFuture<OccupationInquiryResponseBody> actual = investmentAsyncService.fetchOccupationInquiry(investmentRequestHeader, "00000007924129");

        //Then
        CompletableFuture<OccupationInquiryResponseBody> expected = CompletableFuture.completedFuture(occupationInquiryResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_fetch_occupation_inquiry_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.getCustomerOccupationInquiry(any(), anyString())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.fetchOccupationInquiry(any(), anyString());
        });

        //Then
        TMBCommonException expected = new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(),
                HttpStatus.OK,
                null);

        assertEquals(expected.getClass(), actual.getClass());
    }

    @Test
    void should_return_client_relationship_body_when_call_update_client_relationship_given_header_and_crm_id_and_relationship_request() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        RelationshipResponse relationshipResponse = mapper.readValue(Paths.get("src/test/resources/investment/client/relationship.json").toFile(),
                RelationshipResponse.class);
        TmbOneServiceResponse<RelationshipResponseBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(relationshipResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<RelationshipResponseBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);

        RelationshipRequest relationshipRequest = RelationshipRequest.builder()
                .jointType("Single")
                .preferredRedemptionAccountCode("0632964227")
                .preferredRedemptionAccountName("นาง สุนิสา ผลงาม 00000632964227 (SDA)")
                .preferredSubscriptionAccountCode("0632324919")
                .preferredSubscriptionAccountName("นาง สุนิสา ผลงาม 00000632324919 (SDA)")
                .registeredForVat("No")
                .vatEstablishmentBranchCode("ull")
                .withHoldingTaxPreference("TaxWithheld")
                .preferredAddressType("Contact")
                .status("Active")
                .build();
        when(investmentRequestClient.updateClientRelationship(investmentRequestHeader, "00000000002914", relationshipRequest)).thenReturn(response);

        //When
        CompletableFuture<RelationshipResponseBody> actual = investmentAsyncService.updateClientRelationship(
                investmentRequestHeader, "00000000002914", relationshipRequest);

        //Then
        CompletableFuture<RelationshipResponseBody> expected = CompletableFuture.completedFuture(relationshipResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_update_client_relationship_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.updateClientRelationship(any(), anyString(), any())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.updateClientRelationship(any(), anyString(), any());
        });

        //Then
        TMBCommonException expected = new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(),
                HttpStatus.OK,
                null);

        assertEquals(expected.getClass(), actual.getClass());
    }

    @Test
    void should_return_open_portfolio_body_when_call_open_portfolio_given_header_and_crm_id_and_open_portfolio_request() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        OpenPortfolioResponse openPortfolioResponse = mapper.readValue(Paths.get("src/test/resources/investment/portfolio/open_portfolio.json").toFile(),
                OpenPortfolioResponse.class);
        TmbOneServiceResponse<OpenPortfolioResponseBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(openPortfolioResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<OpenPortfolioResponseBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);

        OpenPortfolioRequest openPortfolioRequest = OpenPortfolioRequest.builder()
                .suitabilityScore("5")
                .portfolioType("TMB_ADVTYPE_10_ADVISORY")
                .purposeTypeCode("TMB_PTFPURPOSE_10_RETIREMENT")
                .build();
        when(investmentRequestClient.openPortfolio(investmentRequestHeader, "00000000002914", openPortfolioRequest)).thenReturn(response);

        //When
        CompletableFuture<OpenPortfolioResponseBody> actual = investmentAsyncService.openPortfolio(
                investmentRequestHeader, "00000000002914", openPortfolioRequest);

        //Then
        CompletableFuture<OpenPortfolioResponseBody> expected = CompletableFuture.completedFuture(openPortfolioResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_open_portfolio_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.openPortfolio(any(), anyString(), any())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.openPortfolio(any(), anyString(), any());
        });

        //Then
        TMBCommonException expected = new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(),
                HttpStatus.OK,
                null);

        assertEquals(expected.getClass(), actual.getClass());
    }

    @Test
    void should_return_occupation_body_when_call_update_occupation_given_header_and_crm_id_and_occupation_request() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        OccupationResponse occupationResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/occupation_update.json").toFile(),
                OccupationResponse.class);
        TmbOneServiceResponse<OccupationResponseBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(occupationResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<OccupationResponseBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);

        OccupationRequest occupationRequest = OccupationRequest.builder()
                .occupationCode("406")
                .positionDescription("ผู้ช่วยผู้จัดการ")
                .build();
        when(investmentRequestClient.updateOccupation(investmentRequestHeader, "00000000002914", occupationRequest)).thenReturn(response);

        //When
        CompletableFuture<OccupationResponseBody> actual = investmentAsyncService.updateOccupation(
                investmentRequestHeader, "00000000002914", occupationRequest);

        //Then
        CompletableFuture<OccupationResponseBody> expected = CompletableFuture.completedFuture(occupationResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_update_occupation_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.updateOccupation(any(), anyString(), any())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.updateOccupation(any(), anyString(), any());
        });

        //Then
        TMBCommonException expected = new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(),
                HttpStatus.OK,
                null);

        assertEquals(expected.getClass(), actual.getClass());
    }

    // Test TMBCommonException Handling
    @Test
    void should_throw_common_exception_when_call_fetch_fund_information() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.getFundInformation(any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            investmentAsyncService.fetchFundInformation(any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_call_fetch_fund_DailyNav() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.getFundDailyNav(any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            investmentAsyncService.fetchFundDailyNav(any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_call_fetch_AccountPurpose() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.getCustomerAccountPurpose(any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            investmentAsyncService.fetchAccountPurpose(any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_call_fetch_occupation_inquiry() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.getCustomerOccupationInquiry(any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            investmentAsyncService.fetchOccupationInquiry(any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_get_first_trade() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.getFirstTrade(any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            investmentAsyncService.getFirstTrade(any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_update_client_relationship() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.updateClientRelationship(any(), any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            investmentAsyncService.updateClientRelationship(any(), any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_open_portfolio() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.openPortfolio(any(), any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            investmentAsyncService.openPortfolio(any(), any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    private FeignException mockFeignExceptionBadRequest(String errorCode, String errorMessage) {
        Request.Body body = Request.Body.create("".getBytes(StandardCharsets.UTF_8));
        RequestTemplate template = new RequestTemplate();
        Map<String, Collection<String>> headers = new HashMap<>();
        String errorBody = "{\n" +
                "    \"status\": {\n" +
                "        \"code\": \"" + errorCode + "\",\n" +
                "        \"message\": \"" + errorMessage + "\",\n" +
                "        \"service\": null,\n" +
                "        \"description\": \"Please enter PIN\"\n" +
                "    },\n" +
                "    \"data\": null\n" +
                "}";
        Request request = Request.create(Request.HttpMethod.POST, "http://localhost", headers, body, template);
        FeignException.BadRequest e = new FeignException.BadRequest("", request, errorBody.getBytes(StandardCharsets.UTF_8));
        return e;
    }

}