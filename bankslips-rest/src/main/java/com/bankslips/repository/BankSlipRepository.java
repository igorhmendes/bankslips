package com.bankslips.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankslips.model.BankSlip;

@Repository
public interface BankSlipRepository extends JpaRepository<BankSlip, UUID> {

}
