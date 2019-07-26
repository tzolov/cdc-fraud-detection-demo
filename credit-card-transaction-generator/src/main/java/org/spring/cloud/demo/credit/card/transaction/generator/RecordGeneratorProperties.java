package org.spring.cloud.demo.credit.card.transaction.generator;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Christian Tzolov
 */
@ConfigurationProperties("credit.card.transaction.generator")
public class RecordGeneratorProperties {

	/**
	 * Drops the Schema and Tables on start
	 */
	private boolean dropSchema = true;


	public boolean isDropSchema() {
		return dropSchema;
	}

	public void setDropSchema(boolean dropSchema) {
		this.dropSchema = dropSchema;
	}
}
