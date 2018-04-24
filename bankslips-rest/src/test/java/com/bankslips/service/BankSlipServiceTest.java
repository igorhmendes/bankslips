package com.bankslips.service;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.bankslips.model.BankSlip;
import com.bankslips.model.Status;
import com.bankslips.repository.BankSlipRepository;
import com.bankslips.service.impl.BankSlipServiceImpl;
import com.bankslips.support.exception.BankslipException;
import com.bankslips.support.validation.BeanValidator;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class BankSlipServiceTest {

	@Value("${bankslip.not.provider.code}")
	private Integer bankSlipNotProvider;

	@Value("${bankslip.invalid.fields.code}")
	private Integer invalidFieldsCode;

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
	public void insertBankSlipSucessTest() {
		bankSlipService.insert(newBankSlip());
	}

	@Test
	public void insertBankSlipExceptionTest() {
		thrown.expect(BankslipException.class);
		thrown.expect(hasProperty("code"));
		thrown.expect(hasProperty("code", is(bankSlipNotProvider)));
		bankSlipService.insert(new BankSlip());
	}

	@Test
	public void insertBankSlipValidationError() {
		// thrown.expect(BankslipException.class);
		// thrown.expect(hasProperty("code"));
		// thrown.expect(hasProperty("code", is(invalidFieldsCode)));
		// BankSlip bankslip = newBankSlip();
		// bankslip.setCustomer(null);
		// bankSlipService.insert(bankslip);
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
