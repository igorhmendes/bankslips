package com.bankslips.service;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.bankslips.model.BankSlip;
import com.bankslips.model.Status;
import com.bankslips.model.request.FetchRequest;
import com.bankslips.repository.BankSlipRepository;
import com.bankslips.service.impl.BankSlipServiceImpl;
import com.bankslips.support.exception.BankslipException;
import com.bankslips.support.handler.Messages;
import com.bankslips.support.validation.BeanValidator;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class BankSlipServiceTest {

	@Value("${bankslip.not.provider.code}")
	private Integer bankSlipNotProvider;

	@Value("${bankslip.invalid.fields.code}")
	private Integer invalidFieldsCode;

	@Value("${bankslip.not.found.code}")
	private Integer bankSlipNotFound;

	@Autowired
	private BankSlipService bankSlipService;

	@MockBean
	private BankSlipRepository bankSlipRepository;

	@MockBean
	private BeanValidator validator;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@TestConfiguration
	static class BankSlipServiceTestContextConfig {
		@Bean
		public BankSlipService bankSlipService() {
			return new BankSlipServiceImpl();
		}
	}

	
	
	@Test
	public void fetchAllBankSlipSucessTest() {
		
		FetchRequest<BankSlip> request = new FetchRequest<>();
		request.setPage(0);
		request.setSize(20);
		BankSlip bankSlip = newBankSlip();
		Mockito.when(bankSlipRepository.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(new PageImpl<>(Arrays.asList(bankSlip)));

		Collection<BankSlip> list =  bankSlipService.fetchAll(request);
		Assert.assertNotNull(list);
		Assert.assertEquals(bankSlip.getId(), list.iterator().next().getId());
	}
	
	@Test
	public void insertBankSlipSucessTest() {
		bankSlipService.insert(newBankSlip());
	}

	@Test
	public void insertBankSlipExceptionTest() {
		thrown.expect(BankslipException.class);
		thrown.expect(hasProperty("code", is(bankSlipNotProvider)));
		bankSlipService.insert(new BankSlip());
	}

	@Test
	public void insertBankSlipValidationError() {

		thrown.expect(BankslipException.class);
		thrown.expect(hasProperty("code", is(invalidFieldsCode)));
		BankSlip bankslip = newBankSlip();
		bankslip.setCustomer(null);
		Mockito.when(validator.validate(bankslip))
				.thenThrow(new BankslipException(invalidFieldsCode, Messages.FIELD_INVALID.getCode()));
		bankSlipService.insert(bankslip);
	}

	@Test
	public void payBankSlipSucessTest() {
		Mockito.when(bankSlipRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(newBankSlip()));
		bankSlipService.payBankSlip(newBankSlip().getId());
	}

	@Test
	public void payBankSlipExceptionTest() {
		thrown.expect(BankslipException.class);
		thrown.expect(hasProperty("code", is(bankSlipNotFound)));
		bankSlipService.payBankSlip(newBankSlip().getId());
	}

	@Test
	public void cancelBankSlipSucessTest() {
		Mockito.when(bankSlipRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(newBankSlip()));
		bankSlipService.cancelBankSlip(newBankSlip().getId());
	}

	@Test
	public void cancelBankSlipExceptionTest() {
		thrown.expect(BankslipException.class);
		thrown.expect(hasProperty("code", is(bankSlipNotFound)));
		bankSlipService.cancelBankSlip(newBankSlip().getId());
	}

	private BankSlip newBankSlip() {
		BankSlip bankSlip = new BankSlip();
		bankSlip.setDueDate(LocalDate.now());
		bankSlip.setCustomer("Test Customer");
		bankSlip.setTotalInCents(10000L);
		bankSlip.setStatus(Status.PENDING);
		return bankSlip;
	}

}
