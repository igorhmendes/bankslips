package com.bankslips.support.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.bankslips.support.handler.I18nHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private I18nHandler i18n;

	@ExceptionHandler({ BankslipException.class })
	protected ResponseEntity<?> handleExceptionInternal(RuntimeException ex, WebRequest request) {

		BankslipException be = (BankslipException) ex;
		String msg = i18n.getMessage(be.getI18nCode(), request.getLocale());
		return new ResponseEntity<>(msg, HttpStatus.valueOf(be.getCode()));
	}

}
