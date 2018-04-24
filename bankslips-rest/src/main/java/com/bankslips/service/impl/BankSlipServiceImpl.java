package com.bankslips.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankslips.model.BankSlip;
import com.bankslips.model.Status;
import com.bankslips.model.request.FetchRequest;
import com.bankslips.repository.BankSlipRepository;
import com.bankslips.service.BankSlipService;
import com.bankslips.support.exception.BankslipException;
import com.bankslips.support.handler.Messages;
import com.bankslips.support.validation.BeanValidator;

@Service
@Transactional
public class BankSlipServiceImpl implements BankSlipService {

	@Autowired
	private BankSlipRepository repository;

	@Autowired
	private BeanValidator validator;

	@Value("${bankslip.not.provider.code}")
	private Integer bankSlipNotProvider;

	@Value("${bankslip.not.found.code}")
	private Integer bankSlipNotFound;

	@Value("${bankslip.days.to.min.fine}")
	private Integer daysToFine;

	@Value("${bankslip.min.fine}")
	private Double minFine;

	@Value("${bankslip.max.fine}")
	private Double maxFine;

	@Override
	public Collection<BankSlip> fetchAll(FetchRequest<BankSlip> request) {
		PageRequest requestPage = PageRequest.of(request.getPage(), request.getSize());
		return repository.findAll(requestPage).getContent();
	}

	@Override
	public void insert(BankSlip bankSlip) {

		validateRequest(bankSlip);
		validator.validate(bankSlip);
		bankSlip.setRegisterDate(LocalDateTime.now());
		bankSlip.setModifiedDate(LocalDateTime.now());
		repository.save(bankSlip);
	}

	@Override
	public void payBankSlip(UUID bankSlipId) {

		BankSlip bankSlip = fetchBankSlip(bankSlipId);
		bankSlip.setStatus(Status.PAID);
		bankSlip.setModifiedDate(LocalDateTime.now());
		repository.save(bankSlip);

	}

	@Override
	public void cancelBankSlip(UUID bankSlipId) {
		BankSlip bankSlip = fetchBankSlip(bankSlipId);
		bankSlip.setStatus(Status.CANCELED);
		bankSlip.setModifiedDate(LocalDateTime.now());
		repository.save(bankSlip);

	}

	@Override
	public BankSlip fetchBankSlip(UUID bankSlipId) {

		BankSlip bankSlip;
		try {
			bankSlip = repository.findById(bankSlipId).get();
		} catch (NoSuchElementException e) {
			throw new BankslipException(bankSlipNotFound, Messages.NOT_FOUND.getCode());
		}

		CalculateFine(bankSlip);
		return bankSlip;
	}

	private void CalculateFine(BankSlip bankSlip) {

		LocalDate dateAfter = LocalDate.now();
		long days = ChronoUnit.DAYS.between(bankSlip.getDueDate(), dateAfter);

		Double juros = days > 0 && days < daysToFine ? minFine : maxFine;
		bankSlip.setFine(Double.valueOf(bankSlip.getTotalInCents() * juros).longValue());

	}

	private void validateRequest(BankSlip bankSlip) {
		if (bankSlip.getCustomer() == null && bankSlip.getDueDate() == null && bankSlip.getStatus() == null
				&& bankSlip.getTotalInCents() == null) {
			throw new BankslipException(bankSlipNotProvider, Messages.BANKSLIP_NOT_PROVIDER.getCode());
		}
	}

}
