package com.bankslips.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

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

	@JsonProperty("due_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate dueDate;

	@Positive
	@NotNull
	@JsonProperty("total_in_cents")
	private Long totalInCents;

	@NotNull
	private String customer;

	@Transient
	private Long fine;

	@NotNull
	private Status status;

	private LocalDateTime registerDate;

	private LocalDateTime modifiedDate;
}
