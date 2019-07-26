package org.spring.cloud.demo.credit.card.transaction.generator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ RecordGeneratorProperties.class })
public class CreditCardTransactionGeneratorApplication implements CommandLineRunner {

	private static final Log logger = LogFactory.getLog(CreditCardTransactionGeneratorApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(CreditCardTransactionGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) {
		logger.info("Start Transaction Generator");
	}

}
