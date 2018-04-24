package com.bankslips.support.handler;

public enum Messages {
	CREATE("api.response.create.bankslip.sucess"),
	PAYMENT("api.response.payment.bankslip.success"),
	CANCEL("api.response.cancel.bankslip.success"),
	BANKSLIP_NOT_PROVIDER("api.response.create.bankslip.not.provide"),
	FIELD_INVALID("api.response.create.bankslip.field.invalid"),
	NOT_FOUND("api.response.detail.bankslip.not.found");
	
	private String code;
	
	private Messages(String code) 
	{
	 this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}
