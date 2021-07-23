package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.common.ob.dropdown.CommonCodeEntry;
import com.tmb.common.model.legacy.rsl.ws.dropdown.response.ResponseDropdown;
import com.tmb.common.model.legacy.rsl.ws.incomemodel.response.Body;
import com.tmb.common.model.legacy.rsl.ws.incomemodel.response.ResponseIncomeModel;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetDropdownListClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetIncomeModelInfoClient;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class LoanSubmissionIncomeInfoServiceTest {

    @Mock
    private LoanSubmissionGetIncomeModelInfoClient loanSubmissionGetIncomeModelInfoClient;
    @Mock
    private CustomerServiceClient customerServiceClient;
    @Mock
    private LoanSubmissionGetDropdownListClient getDropdownListClient;

    LoanSubmissionIncomeInfoService loanSubmissionIncomeInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        loanSubmissionIncomeInfoService = new LoanSubmissionIncomeInfoService(loanSubmissionGetIncomeModelInfoClient, customerServiceClient, getDropdownListClient);
    }

    @Test
    public void testGetIncomeInfoByRmIdNotReturnStatus() throws ServiceException, RemoteException {
        ResponseIncomeModel clientRes = new ResponseIncomeModel();
        Body body = new Body();
        body.setIncomeModelAmt(BigDecimal.valueOf(100));
        clientRes.setBody(body);
        when(loanSubmissionGetIncomeModelInfoClient.getIncomeInfo(any())).thenReturn(clientRes);
        IncomeInfo result = loanSubmissionIncomeInfoService.getIncomeInfoByRmId("rmId");
        assertEquals(BigDecimal.valueOf(100), result.getIncomeAmount());
    }

    @Test
    public void testGetIncomeInfoByRmIdReturnWithStatusSalary() throws ServiceException, RemoteException {
        ResponseIncomeModel clientRes = new ResponseIncomeModel();
        Body body = new Body();
        body.setIncomeModelAmt(BigDecimal.valueOf(100));
        clientRes.setBody(body);
        when(loanSubmissionGetIncomeModelInfoClient.getIncomeInfo(any())).thenReturn(clientRes);

        TmbOneServiceResponse<CustGeneralProfileResponse> customerModuleResponse = new TmbOneServiceResponse<CustGeneralProfileResponse>();
        CustGeneralProfileResponse profile = new CustGeneralProfileResponse();
        profile.setOccupationCode("306");
        customerModuleResponse.setData(profile);
        when(customerServiceClient.getCustomerProfile(any())).thenReturn(ResponseEntity.ok(customerModuleResponse));

        ResponseDropdown dropdown = new ResponseDropdown();
        CommonCodeEntry[] entrycodes = new CommonCodeEntry[1];
        entrycodes[0] = new CommonCodeEntry();
        entrycodes[0].setEntryCode("306");
        entrycodes[0].setExtValue1("01");
        com.tmb.common.model.legacy.rsl.ws.dropdown.response.Body dropdownBody = new com.tmb.common.model.legacy.rsl.ws.dropdown.response.Body();
        dropdownBody.setCommonCodeEntries(entrycodes);
        dropdown.setBody(dropdownBody);
        when(getDropdownListClient.getDropdownList(any())).thenReturn(dropdown);

        IncomeInfo result = loanSubmissionIncomeInfoService.getIncomeInfoByRmId("rmId");
        assertEquals(BigDecimal.valueOf(100), result.getIncomeAmount());
        assertEquals("salary", result.getStatusWorking());
    }

    @Test
    public void testGetIncomeInfoByRmIdReturnWithStatusSelfEmployed() throws ServiceException, RemoteException {
        ResponseIncomeModel clientRes = new ResponseIncomeModel();
        Body body = new Body();
        body.setIncomeModelAmt(BigDecimal.valueOf(100));
        clientRes.setBody(body);
        when(loanSubmissionGetIncomeModelInfoClient.getIncomeInfo(any())).thenReturn(clientRes);

        TmbOneServiceResponse<CustGeneralProfileResponse> customerModuleResponse = new TmbOneServiceResponse<CustGeneralProfileResponse>();
        CustGeneralProfileResponse profile = new CustGeneralProfileResponse();
        profile.setOccupationCode("306");
        customerModuleResponse.setData(profile);
        when(customerServiceClient.getCustomerProfile(any())).thenReturn(ResponseEntity.ok(customerModuleResponse));

        ResponseDropdown dropdown = new ResponseDropdown();
        CommonCodeEntry[] entrycodes = new CommonCodeEntry[1];
        entrycodes[0] = new CommonCodeEntry();
        entrycodes[0].setEntryCode("306");
        entrycodes[0].setExtValue1("02");
        com.tmb.common.model.legacy.rsl.ws.dropdown.response.Body dropdownBody = new com.tmb.common.model.legacy.rsl.ws.dropdown.response.Body();
        dropdownBody.setCommonCodeEntries(entrycodes);
        dropdown.setBody(dropdownBody);
        when(getDropdownListClient.getDropdownList(any())).thenReturn(dropdown);

        IncomeInfo result = loanSubmissionIncomeInfoService.getIncomeInfoByRmId("rmId");
        assertEquals(BigDecimal.valueOf(100), result.getIncomeAmount());
        assertEquals("self_employed", result.getStatusWorking());
    }
}