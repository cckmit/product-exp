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
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.request.AccountRedeemRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response.AccountRedeemResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dailynav.response.DailyNavBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dailynav.response.DailyNavResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.request.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.response.InformationBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.response.InformationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.request.PortfolioNicknameRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.response.PortfolioNicknameResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.response.PortfolioNicknameResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    void should_return_account_redeem_body_when_call_fetch_account_redeem_given_header_and_account_redeem_request() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        AccountRedeemResponse accountPurposeResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/account_redeem.json").toFile(),
                AccountRedeemResponse.class);
        TmbOneServiceResponse<AccountRedeemResponseBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(accountPurposeResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<AccountRedeemResponseBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);

        AccountRedeemRequest accountRedeemRequest = AccountRedeemRequest.builder().crmId("00000007924129").build();
        when(investmentRequestClient.getCustomerAccountRedeem(investmentRequestHeader, accountRedeemRequest)).thenReturn(response);

        //When
        CompletableFuture<AccountRedeemResponseBody> actual = investmentAsyncService.fetchAccountRedeem(investmentRequestHeader, accountRedeemRequest);

        //Then
        CompletableFuture<AccountRedeemResponseBody> expected = CompletableFuture.completedFuture(accountPurposeResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_fetch_account_redeem_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.getCustomerAccountRedeem(any(), any())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.fetchAccountRedeem(any(), any());
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
    void should_return_client_relationship_body_when_call_update_client_relationship_given_header_and_relationship_request() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
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
                .crmId("00000000002914")
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
        when(investmentRequestClient.updateClientRelationship(investmentRequestHeader, relationshipRequest)).thenReturn(response);

        //When
        CompletableFuture<RelationshipResponseBody> actual = investmentAsyncService.updateClientRelationship(investmentRequestHeader, relationshipRequest);

        //Then
        CompletableFuture<RelationshipResponseBody> expected = CompletableFuture.completedFuture(relationshipResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_update_client_relationship_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.updateClientRelationship(any(), any())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.updateClientRelationship(any(), any());
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
    void should_return_open_portfolio_body_when_call_open_portfolio_given_header_and_open_portfolio_request() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
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
                .crmId("00000000002914")
                .suitabilityScore("5")
                .portfolioType("TMB_ADVTYPE_10_ADVISORY")
                .purposeTypeCode("TMB_PTFPURPOSE_10_RETIREMENT")
                .build();
        when(investmentRequestClient.openPortfolio(investmentRequestHeader, openPortfolioRequest)).thenReturn(response);

        //When
        CompletableFuture<OpenPortfolioResponseBody> actual = investmentAsyncService.openPortfolio(investmentRequestHeader, openPortfolioRequest);

        //Then
        CompletableFuture<OpenPortfolioResponseBody> expected = CompletableFuture.completedFuture(openPortfolioResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_open_portfolio_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.openPortfolio(any(), any())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.openPortfolio(any(), any());
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
    void should_return_portfolio_nickname_body_when_call_update_portfolio_nickname_given_header_and_portfolio_nickname_request() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        PortfolioNicknameResponse portfolioNicknameResponse = mapper.readValue(Paths.get("src/test/resources/investment/portfolio/nickname.json").toFile(),
                PortfolioNicknameResponse.class);
        TmbOneServiceResponse<PortfolioNicknameResponseBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(portfolioNicknameResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<PortfolioNicknameResponseBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);

        PortfolioNicknameRequest portfolioNicknameRequest = PortfolioNicknameRequest.builder()
                .portfolioNumber("PT000000000000108261")
                .portfolioNickName("อนาคตเพื่อการศึกษา")
                .build();
        when(investmentRequestClient.updatePortfolioNickname(investmentRequestHeader, portfolioNicknameRequest)).thenReturn(response);

        //When
        CompletableFuture<PortfolioNicknameResponseBody> actual = investmentAsyncService.updatePortfolioNickname(investmentRequestHeader, portfolioNicknameRequest);

        //Then
        CompletableFuture<PortfolioNicknameResponseBody> expected = CompletableFuture.completedFuture(portfolioNicknameResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_null_when_call_update_portfolio_nickname_given_throw_exception_from_api() {
        //Given
        when(investmentRequestClient.openPortfolio(any(), any())).thenThrow(RuntimeException.class);

        //When
        TMBCommonException actual = assertThrows(TMBCommonException.class, () -> {
            investmentAsyncService.openPortfolio(any(), any());
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
}