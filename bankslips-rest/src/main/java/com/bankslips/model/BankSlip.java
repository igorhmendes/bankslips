package com.bankslips.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
public class BankSlip {

	@Id
	private UUID id = UUID.randomUUID();
}
