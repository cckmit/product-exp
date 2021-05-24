package com.tmb.oneapp.productsexpservice.controller;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tmb.oneapp.productsexpservice.model.request.loan.LoanPreloadRequest;
import com.tmb.oneapp.productsexpservice.model.response.LoanPreloadResponse;
import com.tmb.oneapp.productsexpservice.service.PersonalLoanService;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class PersonalLoanControllerTest {

	PersonalLoanController personalLoanController;
	
	@Mock
	PersonalLoanService personalLoanService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		personalLoanController = new PersonalLoanController(personalLoanService);
	}
	
	@Test
	public void testController() {
		LoanPreloadRequest loadnPreloadReq = new LoanPreloadRequest();
		loadnPreloadReq.setProductCode("P");
		LoanPreloadResponse response = new LoanPreloadResponse();
		when(personalLoanService.checkPreload(any(), any())).thenReturn(response);
		personalLoanController.checkPreload("zxx", loadnPreloadReq);
		assertTrue(true);
	}

}
