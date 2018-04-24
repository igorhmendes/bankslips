package com.bankslips.api.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import java.util.Collection;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.bankslips.model.BankSlip;
import com.bankslips.model.request.FetchRequest;
import com.bankslips.model.response.Response;
import com.bankslips.repository.BankSlipRepository;
import com.bankslips.service.impl.BankSlipServiceImpl;
import com.bankslips.support.handler.I18nHandler;
import com.bankslips.support.handler.Messages;

@RestController
@RequestMapping("/rest/bankslips")
public class BankslipApi {

	@Autowired
	private BankSlipServiceImpl bankSlipService;

	@Autowired
	private BankSlipRepository repository;

	@Autowired
	private I18nHandler i18n;

	@ResponseBody
	@GetMapping("/{page}/{size}")
	public ResponseEntity<Collection<BankSlip>> fetchAll(@PathVariable("page") Integer page,
			@PathVariable("size") Integer size) {

		FetchRequest<BankSlip> request = new FetchRequest<>();
		request.setPage(page);
		request.setSize(size);

		return new ResponseEntity<Collection<BankSlip>>(bankSlipService.fetchAll(request), OK);

	}

	@ResponseBody
	@GetMapping
	public ResponseEntity<Collection<BankSlip>> fetchAll() {

		return new ResponseEntity<Collection<BankSlip>>(repository.findAll(), OK);

	}

	@PostMapping
	@ResponseBody
	public ResponseEntity<Response> insert(@RequestBody BankSlip bankSlip, WebRequest request) {
		bankSlipService.insert(bankSlip);
		String message = i18n.getMessage(Messages.CREATE.getCode(), request.getLocale());
		return new ResponseEntity<Response>(Response.builder().message(message).statusCode(CREATED.value()).build(),
				CREATED);
	}

	@GetMapping("/{id}")
	@ResponseBody
	public ResponseEntity<BankSlip> fetchById(@PathVariable("id") UUID bankSlipId, WebRequest request) {
		return new ResponseEntity<BankSlip>(bankSlipService.fetchBankSlip(bankSlipId), OK);
	}

	@PutMapping("/{id}/pay")
	@ResponseBody
	public ResponseEntity<Response> payBankSlip(@PathVariable("id") UUID bankSlipId, WebRequest request) {
		bankSlipService.payBankSlip(bankSlipId);
		String message = i18n.getMessage(Messages.PAYMENT.getCode(), request.getLocale());
		return new ResponseEntity<Response>(Response.builder().message(message).statusCode(OK.value()).build(), OK);
	}

	@DeleteMapping("/{id}/cancel")
	@ResponseBody
	public ResponseEntity<Response> cancelBankSlip(@PathVariable("id") UUID bankSlipId, WebRequest request) {
		bankSlipService.cancelBankSlip(bankSlipId);
		String message = i18n.getMessage(Messages.CANCEL.getCode(), request.getLocale());
		return new ResponseEntity<Response>(Response.builder().message(message).statusCode(OK.value()).build(), OK);
	}
}
