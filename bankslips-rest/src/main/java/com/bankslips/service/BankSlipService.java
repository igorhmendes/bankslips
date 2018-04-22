package com.bankslips.service;

import java.util.Collection;
import java.util.UUID;

import com.bankslips.model.BankSlip;
import com.bankslips.model.request.FetchRequest;

public interface BankSlipService {

	Collection<BankSlip> fetchAll(FetchRequest<BankSlip> request);

	void insert(BankSlip bankSlip);

	void payBankSlip(UUID bankSlipId);

	void cancelBankSlip(UUID bankSlipId);

	BankSlip fetchBankSlip(UUID bankSlipId);
}
