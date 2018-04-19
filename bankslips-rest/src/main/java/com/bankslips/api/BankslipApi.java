package com.bankslips.api;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bankslips.model.BankSlip;
import com.bankslips.repository.BankslipsRepository;

@RestController
@RequestMapping("/rest/bankslips")
public class BankslipApi {

	@Autowired
	private BankslipsRepository repository;

	@ResponseBody
	@GetMapping("/fetchAll")
	public ResponseEntity<Collection<BankSlip>> fetchAll() {
		return ResponseEntity.ok(repository.findAll());
	}
}
