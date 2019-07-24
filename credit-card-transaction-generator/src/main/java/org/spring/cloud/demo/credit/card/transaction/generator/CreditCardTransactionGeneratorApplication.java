package org.spring.cloud.demo.credit.card.transaction.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CreditCardTransactionGeneratorApplication implements CommandLineRunner {

	@Autowired
	private RecordGenerator recordGenerator;

	public static void main(String[] args) {
		SpringApplication.run(CreditCardTransactionGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) {
		this.recordGenerator.createCreditCardTransactionDatabase(true);
	}

}
