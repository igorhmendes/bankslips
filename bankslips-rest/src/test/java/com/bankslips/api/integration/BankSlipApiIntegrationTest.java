package com.bankslips.api.integration;

import static com.bankslips.support.handler.JsonHandler.toJson;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.bankslips.BankslipsApplication;
import com.bankslips.model.BankSlip;
import com.bankslips.model.Status;
import com.bankslips.repository.BankSlipRepository;
import com.bankslips.support.handler.I18nHandler;
import com.bankslips.support.handler.Messages;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = BankslipsApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integration.properties")
public class BankSlipApiIntegrationTest {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Autowired
	private BankSlipRepository repository;

	@Autowired
	private I18nHandler i18n;

	@Autowired
	private MockMvc mvc;

	@Before
	public void setup() {
		repository.deleteAll();
	}

	@Test
	public void insertBankSlip201Test() throws Exception {
		BankSlip bankSlip = newBankSlip();
		String message = i18n.getMessage(Messages.CREATE.getCode(), Locale.getDefault());
		mvc.perform(post("/rest/bankslips").content(toJson(bankSlip)).contentType(contentType)).andDo(print())
				.andExpect(status().isCreated()).andExpect(content().contentTypeCompatibleWith(contentType))
				.andExpect(jsonPath("$.message", is(message)));
		Assert.assertEquals(Boolean.TRUE, repository.findById(bankSlip.getId()).isPresent());
	}

	@Test
	public void insertBankSlip422Test() throws Exception {
		BankSlip bankSlip = newBankSlip();
		bankSlip.setCustomer(null);
		String message = i18n.getMessage(Messages.FIELD_INVALID.getCode(), Locale.getDefault());
		mvc.perform(post("/rest/bankslips").content(toJson(bankSlip)).contentType(contentType)).andDo(print())
				.andExpect(status().isUnprocessableEntity()).andExpect(content().contentTypeCompatibleWith(contentType))
				.andExpect(jsonPath("$.message", is(message)));
	}

	@Test
	public void insertBankSlip400Test() throws Exception {
		BankSlip bankSlip = new BankSlip();
		String message = i18n.getMessage(Messages.BANKSLIP_NOT_PROVIDER.getCode(), Locale.getDefault());
		mvc.perform(post("/rest/bankslips").content(toJson(bankSlip)).contentType(contentType)).andDo(print())
				.andExpect(status().isBadRequest()).andExpect(content().contentTypeCompatibleWith(contentType))
				.andExpect(jsonPath("$.message", is(message)));
	}

	@Test
	public void fetchBankSlipById200Test() throws Exception {
		BankSlip bankSlip = newBankSlip();
		insertBankSlip(bankSlip);

		mvc.perform(get("/rest/bankslips/" + bankSlip.getId()).content(toJson(bankSlip)).contentType(contentType))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(contentType))
				.andExpect(jsonPath("$.customer", is(bankSlip.getCustomer())));
	}

	@Test
	public void fetchBankSlipById404Test() throws Exception {
		BankSlip bankSlip = newBankSlip();
		String message = i18n.getMessage(Messages.NOT_FOUND.getCode(), Locale.getDefault());
		mvc.perform(get("/rest/bankslips/" + bankSlip.getId()).content(toJson(bankSlip)).contentType(contentType))
				.andDo(print()).andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(contentType))
				.andExpect(jsonPath("$.message", is(message)));
	}

	@Test
	public void payBankSlipById200Test() throws Exception {
		BankSlip bankSlip = newBankSlip();
		insertBankSlip(bankSlip);
		String message = i18n.getMessage(Messages.PAYMENT.getCode(), Locale.getDefault());
		mvc.perform(put("/rest/bankslips/" + bankSlip.getId() + "/pay").contentType(contentType)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(contentType))
				.andExpect(jsonPath("$.message", is(message)));

		Optional<BankSlip> bankSlipUpdated = repository.findById(bankSlip.getId());
		Assert.assertEquals(Boolean.TRUE, bankSlipUpdated.isPresent());
		Assert.assertEquals(Status.PAID, bankSlipUpdated.get().getStatus());

	}

	@Test
	public void payBankSlipById404Test() throws Exception {
		BankSlip bankSlip = newBankSlip();
		String message = i18n.getMessage(Messages.NOT_FOUND.getCode(), Locale.getDefault());
		mvc.perform(put("/rest/bankslips/" + bankSlip.getId() + "/pay").contentType(contentType)).andDo(print())
				.andExpect(status().isNotFound()).andExpect(content().contentTypeCompatibleWith(contentType))
				.andExpect(jsonPath("$.message", is(message)));
	}

	@Test
	public void cancelBankSlipById200Test() throws Exception {
		BankSlip bankSlip = newBankSlip();
		insertBankSlip(bankSlip);
		String message = i18n.getMessage(Messages.CANCEL.getCode(), Locale.getDefault());
		mvc.perform(delete("/rest/bankslips/" + bankSlip.getId() + "/cancel").contentType(contentType)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(contentType))
				.andExpect(jsonPath("$.message", is(message)));

		Optional<BankSlip> bankSlipUpdated = repository.findById(bankSlip.getId());
		Assert.assertEquals(Boolean.TRUE, bankSlipUpdated.isPresent());
		Assert.assertEquals(Status.CANCELED, bankSlipUpdated.get().getStatus());

	}

	@Test
	public void cancelBankSlipById404Test() throws Exception {
		BankSlip bankSlip = newBankSlip();
		String message = i18n.getMessage(Messages.NOT_FOUND.getCode(), Locale.getDefault());
		mvc.perform(delete("/rest/bankslips/" + bankSlip.getId() + "/cancel").contentType(contentType)).andDo(print())
				.andExpect(status().isNotFound()).andExpect(content().contentTypeCompatibleWith(contentType))
				.andExpect(jsonPath("$.message", is(message)));
	}

	private void insertBankSlip(BankSlip bankSlip) {
		repository.save(bankSlip);
	}

	private static BankSlip newBankSlip() {
		BankSlip bankSlip = new BankSlip();
		bankSlip.setDueDate(LocalDate.now());
		bankSlip.setCustomer("Test Customer");
		bankSlip.setTotalInCents(10000L);
		bankSlip.setStatus(Status.PENDING);
		return bankSlip;
	}

}
