package com.bankslips;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.bankslips" })
public class BankslipsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankslipsApplication.class, args);
	}
}
