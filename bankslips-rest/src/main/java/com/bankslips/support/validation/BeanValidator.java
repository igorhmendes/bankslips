package com.bankslips.support.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bankslips.support.exception.BankslipException;
import com.bankslips.support.handler.Messages;

@Component
public class BeanValidator {

	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Value("${bankslip.invalid.fields.code}")
	private Integer invalidFieldsCode;

	public <T> boolean validate(T bean) {
		Set<ConstraintViolation<T>> violations = validator.validate(bean);

		if (violations == null || violations.isEmpty()) {
			return true;
		}

		throw new BankslipException(invalidFieldsCode, Messages.FIELD_INVALID.getCode());
	}
}