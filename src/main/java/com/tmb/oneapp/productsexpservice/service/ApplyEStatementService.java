package com.tmb.oneapp.productsexpservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.creditcard.CreditCardDetail;
import com.tmb.common.model.creditcard.GetCardsBalancesResponse;
import com.tmb.common.model.customer.UpdateEStatmentRequest;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.applyestatement.ApplyEStatementResponse;
import com.tmb.oneapp.productsexpservice.model.applyestatement.ProductHoldingsResp;
import com.tmb.oneapp.productsexpservice.model.applyestatement.StatementFlag;

@Service
public class ApplyEStatementService {

	private static final TMBLogger<ApplyEStatementService> logger = new TMBLogger<>(ApplyEStatementService.class);
	private CustomerServiceClient customerServiceClient;
	private CreditCardClient creditCardClient;
	private AccountRequestClient accountReqClient;
	private ActivitylogService activitylogService;
	public static final String CREDIT_CARD_TYPE = "1";
	public static final String FLASH_CARD_TYPE = "2";

	@Value("${label.product-group.creditcard.th}")
	private String groupCreditCardTh;
	@Value("${label.product-group.flashcard.th}")
	private String groupFlashCardTh;
	@Value("${label.product-group.loan.th}")
	private String groupLoanProductTh;
	@Value("${label.product-group.creditcard.en}")
	private String groupCreditCardEn;
	@Value("${label.product-group.flashcard.en}")
	private String groupFlashCardEn;
	@Value("${label.product-group.loan.en}")
	private String groupLoanProductEn;

	public ApplyEStatementService(CustomerServiceClient customerServiceClient, CreditCardClient creditCardClient,
			AccountRequestClient accountReqClient, ActivitylogService activitylogService) {
		this.customerServiceClient = customerServiceClient;
		this.creditCardClient = creditCardClient;
		this.accountReqClient = accountReqClient;
		this.activitylogService = activitylogService;
	}

	/**
	 * get e-statement
	 * 
	 * @param correlationId
	 * @param crmId
	 * @return
	 */
	public ApplyEStatementResponse getEStatement(String crmId, String correlationId) {
		ResponseEntity<TmbOneServiceResponse<ApplyEStatementResponse>> result = customerServiceClient
				.getCustomerEStatement(crmId, correlationId);
		return result.getBody().getData();
	}

	/**
	 * Update e statement by crm id
	 * 
	 * @param crmId
	 * @param correlationId
	 * @param updateEstatementReq
	 * @param productGroupTH
	 * @param productGroupEN
	 * @return
	 * @throws TMBCommonException
	 */
	public ApplyEStatementResponse updateEstatement(String crmId, String correlationId,
			UpdateEStatmentRequest updateEstatementReq) throws TMBCommonException {
		ApplyEStatementResponse currentEstatementResponse = getEStatement(crmId, correlationId);
		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, correlationId);
		requestHeaders.put(ProductsExpServiceConstant.X_CRMID, crmId);

		StatementFlag statementFlag = currentEstatementResponse.getCustomer().getStatementFlag();

		constructStatementFlagReq(requestHeaders, statementFlag, updateEstatementReq, currentEstatementResponse);
		try {
			if (StringUtils.isNoneBlank(updateEstatementReq.getAccountId())) {
				updateEStatementOnSilverLake(crmId, correlationId, updateEstatementReq);
			}

			ResponseEntity<TmbOneServiceResponse<ApplyEStatementResponse>> response = customerServiceClient
					.updateEStatement(requestHeaders, statementFlag);
			if (!ResponseCode.SUCESS.getCode().equals(response.getBody().getStatus().getCode())) {
				throw new TMBCommonException("Fail on update EC system");
			}
			if (StringUtils.isNotEmpty(updateEstatementReq.getAccountId())) {
				activitylogService.updatedEStatmentCard(updateEstatementReq.getAccountId(), true);
			} else {
				activitylogService.updatedEStatmentLoan(updateEstatementReq.getLoanId(), true);
			}

		} catch (Exception e) {
			logger.error(e.toString(), e);
			if (StringUtils.isNotEmpty(updateEstatementReq.getLoanId())) {
				activitylogService.updatedEStatmentLoan(updateEstatementReq.getLoanId(), false);
			}
			rollBackSilverlake(crmId, correlationId, updateEstatementReq);
			throw new TMBCommonException(e.getMessage());
		}
		return currentEstatementResponse;
	}

	/**
	 * contruct Statment flag update
	 * 
	 * @param requestHeaders
	 * @param currentEstatementResponse
	 * @param statementFlag
	 * @param updateEstatementReq
	 */
	private void constructStatementFlagReq(Map<String, String> requestHeaders, StatementFlag statementFlag,
			UpdateEStatmentRequest updateEstatementReq, ApplyEStatementResponse currentEstatementResponse) {

		String crmId = requestHeaders.get(ProductsExpServiceConstant.X_CRMID);
		ResponseEntity<TmbOneServiceResponse<ProductHoldingsResp>> accountResponse = accountReqClient
				.getProductHoldingService(requestHeaders, crmId);

		List<Object> loanProducts = accountResponse.getBody().getData().getLoanAccounts();
		if (CollectionUtils.isNotEmpty(loanProducts) && StringUtils.isNotEmpty(updateEstatementReq.getAccountId())) {
			statementFlag.setECashToGoStatementFlag("Y");
			currentEstatementResponse.setProductGroupTH(groupLoanProductTh);
			currentEstatementResponse.setProductGroupEN(groupLoanProductEn);
			currentEstatementResponse.setAccountTypeLabel(groupLoanProductTh);
		}
		ResponseEntity<GetCardsBalancesResponse> cardBalanceResponse = creditCardClient
				.getCreditCardBalance(requestHeaders, crmId);

		boolean hasCreditcard = lookUpCardbyType(cardBalanceResponse.getBody(), CREDIT_CARD_TYPE,
				updateEstatementReq.getAccountId());
		if (hasCreditcard) {
			statementFlag.setECreditcardStatementFlag("Y");
			generatedFillUpProductype(updateEstatementReq, "CC");
			currentEstatementResponse.setProductGroupTH(groupCreditCardTh);
			currentEstatementResponse.setProductGroupEN(groupCreditCardEn);
			currentEstatementResponse.setAccountTypeLabel(groupCreditCardTh);
		}
		boolean hasFlashCard = lookUpCardbyType(cardBalanceResponse.getBody(), FLASH_CARD_TYPE,
				updateEstatementReq.getAccountId());
		if (hasFlashCard) {
			generatedFillUpProductype(updateEstatementReq, "FL");
			statementFlag.setEReadyCashStatementFlag("Y");
			currentEstatementResponse.setProductGroupTH(groupFlashCardTh);
			currentEstatementResponse.setProductGroupEN(groupFlashCardEn);
			currentEstatementResponse.setAccountTypeLabel(groupFlashCardTh);
		}
	}

	/**
	 * Update product type supported
	 * 
	 * @param updateEstatementReq
	 * @param productCode
	 */
	private void generatedFillUpProductype(UpdateEStatmentRequest updateEstatementReq, String productCode) {
		List<String> productTypes = updateEstatementReq.getProductType();
		if (CollectionUtils.isEmpty(productTypes)) {
			productTypes = new ArrayList<>();
		}
		productTypes.add(productCode);
		updateEstatementReq.setProductType(productTypes);
	}

	/**
	 * Return true if found
	 * 
	 * @param body
	 * @param creditCardType
	 * @param acccountId
	 * @return
	 */
	private boolean lookUpCardbyType(GetCardsBalancesResponse body, String creditCardType, String acccountId) {
		boolean isFound = false;
		List<CreditCardDetail> creditCardDetail = body.getCreditCard();
		if (CollectionUtils.isNotEmpty(body.getCreditCard())) {
			Optional<CreditCardDetail> creditCardDetailOpt = creditCardDetail.stream()
					.filter(e -> e.getAccountId().equals(acccountId)).collect(Collectors.toList()).stream().findFirst();
			if (creditCardDetailOpt.isPresent()) {
				isFound = creditCardDetailOpt.get().getCardStatus().getCardPloanFlag().equals(creditCardType);
			}
		}

		return isFound;
	}

	/**
	 * Roll back
	 * 
	 * @param crmId
	 * @param correlationId
	 * @param updateEstatementReq
	 */
	private void rollBackSilverlake(String crmId, String correlationId, UpdateEStatmentRequest updateEstatementReq) {
		logger.info("### ROLL BACK SILVERLAKE FOR {} ### ", crmId);
		Map<String, String> headers = new HashMap<>();
		headers.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, correlationId);
		headers.put(ProductsExpServiceConstant.X_CRMID, crmId);
		creditCardClient.cancelEnableEStatement(headers, updateEstatementReq);
	}

	/**
	 * Update e statment on silverlake
	 * 
	 * @param crmId
	 * @param correlationId
	 * @param updateEstatementReq
	 * @throws Exception
	 */
	private void updateEStatementOnSilverLake(String crmId, String correlationId,
			UpdateEStatmentRequest updateEstatementReq) throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, correlationId);
		headers.put(ProductsExpServiceConstant.X_CRMID, crmId);
		String errorCode = null;
		try {
			ResponseEntity<TmbOneServiceResponse<ApplyEStatementResponse>> responseEmail = creditCardClient
					.updateEmailEStatement(headers, updateEstatementReq);

			if (!ResponseCode.SUCESS.getCode().equals(responseEmail.getBody().getStatus().getCode())) {
				errorCode = responseEmail.getBody().getStatus().getCode();
				throw new TMBCommonException(responseEmail.getBody().getStatus().getCode(),
						responseEmail.getBody().getStatus().getMessage(),
						responseEmail.getBody().getStatus().getService(), HttpStatus.BAD_REQUEST, new Exception());
			}
			ResponseEntity<TmbOneServiceResponse<ApplyEStatementResponse>> responseEstatment = creditCardClient
					.updateEnableEStatement(headers, updateEstatementReq);
			if (!ResponseCode.SUCESS.getCode().equals(responseEstatment.getBody().getStatus().getCode())) {
				errorCode = responseEmail.getBody().getStatus().getCode();
				throw new TMBCommonException(responseEstatment.getBody().getStatus().getCode(),
						responseEstatment.getBody().getStatus().getMessage(),
						responseEstatment.getBody().getStatus().getService(), HttpStatus.BAD_REQUEST, new Exception());
			}

		} catch (Exception e) {
			activitylogService.updatedEStatmentCard(errorCode, false);
			throw e;
		}

	}

	public String getEmailStatementFlag(String crmId, String correlationId, String accountId,
			ApplyEStatementResponse applyEStatementResponse) {
		String result = "";
		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, correlationId);
		requestHeaders.put(ProductsExpServiceConstant.X_CRMID, crmId);

		ResponseEntity<TmbOneServiceResponse<ProductHoldingsResp>> accountResponse = accountReqClient
				.getProductHoldingService(requestHeaders, crmId);

		List<Object> loanProducts = accountResponse.getBody().getData().getLoanAccounts();
		if (CollectionUtils.isNotEmpty(loanProducts)) {
			result = applyEStatementResponse.getCustomer().getStatementFlag().getECashToGoStatementFlag();
		}
		ResponseEntity<GetCardsBalancesResponse> cardBalanceResponse = creditCardClient
				.getCreditCardBalance(requestHeaders, crmId);

		boolean hasCreditcard = lookUpCardbyType(cardBalanceResponse.getBody(), CREDIT_CARD_TYPE, accountId);
		if (hasCreditcard) {
			result = applyEStatementResponse.getCustomer().getStatementFlag().getECreditcardStatementFlag();
		}
		boolean hasFlashCard = lookUpCardbyType(cardBalanceResponse.getBody(), FLASH_CARD_TYPE, accountId);
		if (hasFlashCard) {
			result = applyEStatementResponse.getCustomer().getStatementFlag().getEReadyCashStatementFlag();
		}
		return result;
	}

}
