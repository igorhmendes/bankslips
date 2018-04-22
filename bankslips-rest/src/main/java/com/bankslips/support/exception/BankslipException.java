package com.bankslips.support.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BankslipException extends RuntimeException {
	private static final long serialVersionUID = 7027919385392054729L;

	private Integer code;
	private String i18nCode;

	public BankslipException(Integer codeInpute, String i18nCodeInput) {
		setCode(codeInpute);
		setI18nCode(i18nCodeInput);
	}

}
