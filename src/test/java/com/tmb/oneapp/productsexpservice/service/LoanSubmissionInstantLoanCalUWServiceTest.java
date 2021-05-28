package com.tmb.oneapp.productsexpservice.service;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LoanSubmissionInstantLoanCalUWServiceTest {
//    @Mock
//    private LoanSubmissionInstantLoanCalUWClient loanCalUWClient;
//
//    LoanSubmissionInstantLoanCalUWService loanCalUWService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.initMocks(this);
//        loanCalUWService = new LoanSubmissionInstantLoanCalUWService(loanCalUWClient);
//    }
//
//    @Test
//    public void testCheckCalculateUnderwritingApprove() throws ServiceException, RemoteException {
//        RequestInstantLoanCalUW request = new RequestInstantLoanCalUW();
//
//        Header header = new Header();
//        header.setChannel("MIB");
//        header.setModule("3");
//        header.setRequestID(UUID.randomUUID().toString());
//
//        Body body = new Body();
//        body.setTriggerFlag("Y");
//        body.setCaId(BigDecimal.TEN);
//
//        request.setHeader(header);
//        request.setBody(body);
//
//        ResponseInstantLoanCalUW response = new ResponseInstantLoanCalUW();
//        ApprovalMemoCreditCard[] approvalMemoCreditCardList = new ApprovalMemoCreditCard[10];
//        ApprovalMemoCreditCard approvalMemoCreditCard = new ApprovalMemoCreditCard();
//        approvalMemoCreditCard.setCardType("T");
//        approvalMemoCreditCard.setCreditLimit(BigDecimal.ONE);
//        approvalMemoCreditCard.setUnderwritingResult(APPROVE);
//        approvalMemoCreditCard.setCcId(BigDecimal.TEN);
//        approvalMemoCreditCard.setCycleCutDate("20/05/2021");
//        approvalMemoCreditCard.setDebitAccountName("ttb");
//        approvalMemoCreditCard.setDebitAccountNo("111");
//        approvalMemoCreditCard.setFirstPaymentDueDate("21/05/21");
//        approvalMemoCreditCard.setId(BigDecimal.ONE);
//        approvalMemoCreditCard.setPaymentMethod("f");
//        approvalMemoCreditCard.setPayDate("22/05/21");
//        approvalMemoCreditCardList[0] = approvalMemoCreditCard;
//
//        ApprovalMemoFacility[] approvalMemoFacilities = new ApprovalMemoFacility[5];
//        ApprovalMemoFacility approvalMemoFacility = new ApprovalMemoFacility();
//        approvalMemoFacility.setTenor(BigDecimal.TEN);
//        approvalMemoFacility.setCreditLimit(BigDecimal.ONE);
//        approvalMemoFacility.setCycleCutDate("11/11/11");
//        approvalMemoFacility.setId(BigDecimal.ONE);
//        approvalMemoFacility.setUnderwritingResult(APPROVE);
//        approvalMemoFacility.setDisburstAccountName("ttb");
//        approvalMemoFacility.setDisburstAccountNo("11");
//        approvalMemoFacility.setFacId(BigDecimal.TEN);
//        approvalMemoFacility.setFirstPaymentDueDate("11/11/11");
//        approvalMemoFacility.setInstallmentAmount(BigDecimal.TEN);
//        approvalMemoFacility.setInterestRate(BigDecimal.TEN);
//        approvalMemoFacility.setOutstandingBalance(BigDecimal.TEN);
//        approvalMemoFacility.setRateTypePercent(BigDecimal.TEN);
//        approvalMemoFacility.setRateType("Y");
//        approvalMemoFacilities[0] = approvalMemoFacility;
//
//        com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.Body responseBody = new com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.Body();
//        responseBody.setUnderwritingResult(APPROVE);
//        responseBody.setApprovalMemoCreditCards(approvalMemoCreditCardList);
//        responseBody.setApprovalMemoFacilities(approvalMemoFacilities);
//        response.setBody(responseBody);
//
//        when(loanCalUWClient.getCalculateUnderwriting(request)).thenReturn(response);
//        InstantLoanCalUWResponse instantLoanCalUWResponse = loanCalUWService.checkCalculateUnderwriting(request);
//        Assert.assertEquals(response.getBody(), instantLoanCalUWResponse);
//
//    }
}
