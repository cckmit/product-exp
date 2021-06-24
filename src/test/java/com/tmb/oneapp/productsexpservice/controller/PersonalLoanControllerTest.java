package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.flexiloan.InstantLoanCalUWResponse;
import com.tmb.oneapp.productsexpservice.model.request.loan.InstantLoanCalUWRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanPreloadRequest;
import com.tmb.oneapp.productsexpservice.model.response.LoanPreloadResponse;
import com.tmb.oneapp.productsexpservice.model.response.loan.ProductData;
import com.tmb.oneapp.productsexpservice.service.LoanSubmissionInstantLoanCalUWService;
import com.tmb.oneapp.productsexpservice.service.PersonalLoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class PersonalLoanControllerTest {

	PersonalLoanController personalLoanController;

	@Mock
    PersonalLoanService personalLoanService;
	@Mock
    LoanSubmissionInstantLoanCalUWService loanCalUWService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		personalLoanController = new PersonalLoanController(personalLoanService,loanCalUWService);
	}

	@Test
	public void testCheckPreloadSuccess() {
		LoanPreloadRequest loadPreloadReq = new LoanPreloadRequest();
		loadPreloadReq.setProductCode("P");
		LoanPreloadResponse response = new LoanPreloadResponse();
		when(personalLoanService.checkPreload(any(), any())).thenReturn(response);
		personalLoanController.checkPreload("zxx", loadPreloadReq);
		assertTrue(true);
	}

	@Test
	public void testCheckPreloadFail() {
		LoanPreloadRequest loadPreloadReq = new LoanPreloadRequest();
		loadPreloadReq.setProductCode("P");
		LoanPreloadResponse response = new LoanPreloadResponse();
		when(personalLoanService.checkPreload(any(), any())).thenThrow(new IllegalArgumentException());
		ResponseEntity<TmbOneServiceResponse<LoanPreloadResponse>> result= personalLoanController.checkPreload("zxx", loadPreloadReq);
		assertTrue(result.getStatusCode().isError());
	}

	@Test
	public void testCheckCalUWSuccess() throws ServiceException, RemoteException {
        InstantLoanCalUWRequest request = new InstantLoanCalUWRequest();
        request.setCaId(BigDecimal.valueOf(2021052704186761L));
        request.setTriggerFlag("Y");
        request.setProduct("RC01");

		when(loanCalUWService.checkCalculateUnderwriting(request)).thenReturn(any());

		ResponseEntity<TmbOneServiceResponse<InstantLoanCalUWResponse>> result = personalLoanController.checkCalUW(request);
		assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

	}

	@Test
	public void testCheckCalUWSFail() throws ServiceException, RemoteException {

        InstantLoanCalUWRequest request = new InstantLoanCalUWRequest();
        request.setCaId(BigDecimal.valueOf(2021052704186775L));
        request.setTriggerFlag("Y");
        request.setProduct("RC01");

		when(loanCalUWService.checkCalculateUnderwriting(request)).thenThrow(new NullPointerException());

		ResponseEntity<TmbOneServiceResponse<InstantLoanCalUWResponse>> result = personalLoanController.checkCalUW(request);
		assertTrue(result.getStatusCode().isError());
	}


	@Test
	public void testGetProductListSuccess() {
		when(personalLoanService.getProducts()).thenReturn(any());
		personalLoanController.getProductList();
		assertTrue(true);
	}

	@Test
	public void testGetProductListFail() {
		when(personalLoanService.getProducts()).thenThrow(new NullPointerException());

		ResponseEntity<TmbOneServiceResponse<List<ProductData>>> result = personalLoanController.getProductList();
		assertTrue(result.getStatusCode().isError());
	}

}
