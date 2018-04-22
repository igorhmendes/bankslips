package com.bankslips.api.integration;

import static com.bankslips.support.handler.JsonHandler.toJson;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Locale;

import org.junit.Assert;
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
import com.bankslips.repository.BankslipsRepository;
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
	private BankslipsRepository repository;

	@Autowired
	private I18nHandler i18n;

	@Autowired
	private MockMvc mvc;

	@Test
	public void insertBankSlip201Test() throws Exception {
		BankSlip bankSlip = newBankSlip();
		String message = i18n.getMessage(Messages.CREATE.getCode(), Locale.getDefault());
		mvc.perform(post("/rest/bankslips").content(toJson(bankSlip)).contentType(contentType)).andDo(print())
				.andExpect(status().isCreated()).andExpect(content().contentTypeCompatibleWith(contentType))
				.andExpect(jsonPath("$.message", is(message)));
		Assert.assertEquals(Boolean.TRUE, repository.findById(bankSlip.getId()).isPresent());
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
