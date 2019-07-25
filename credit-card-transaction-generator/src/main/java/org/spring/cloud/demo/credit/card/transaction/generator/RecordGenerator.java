package org.spring.cloud.demo.credit.card.transaction.generator;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Christian Tzolov
 */
@Component
public class RecordGenerator {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	public void start(GeneratorControlView mainView) {

		executorService.submit(() -> {
			Assert.notNull(jdbcTemplate, "not null");

			try {
				List<String[]> normalRecords = CsvUtils.readCsv("classpath:/data/creditcard-normal.csv", true);
				List<String[]> fraudRecords = CsvUtils.readCsv("classpath:/data/creditcard-fraud.csv", true);

				Random random = new Random();
				while (mainView.isStopped() == false) {
					String[] record;
					int pct = random.nextInt(100) + 1;
					if (pct >= mainView.getFraudPercentage()) {
						record = normalRecords.get(random.nextInt(normalRecords.size()));
					}
					else {
						record = fraudRecords.get(random.nextInt(fraudRecords.size()));
						//RecordGenerator.this.mainView.incrementFraudCount();
					}
					RecordGenerator.this.insertCreditCardTransaction(record);

					Thread.sleep(1000 * (random.nextInt((int) mainView.getMaxWaitSecond()) +
							mainView.getMinWaitSecond()));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});

		//List<String> result = jdbcTemplate.query("select \"time\", \"amount\" from \"cdc\".\"credit_card_transaction\"",
		//		(rs, i) -> rs.getString("time") + " : " + rs.getString("amount") + " \n");
		//
		//System.out.println(result);

	}

	public void insertCreditCardTransaction(String... record) {
		Assert.isTrue(record.length == CsvUtils.columns.length - 1,
				"Incorrect number of columns in record: " + record.length);

		jdbcTemplate.execute("INSERT INTO \"cdc\".\"credit_card_transaction\" (\"time\",\"v1\",\"v2\",\"v3\",\"v4\",\"v5\"," +
				"\"v6\",\"v7\",\"v8\",\"v9\",\"v10\",\"v11\",\"v12\",\"v13\",\"v14\",\"v15\",\"v16\",\"v17\",\"v18\"" +
				",\"v19\",\"v20\",\"v21\",\"v22\",\"v23\",\"v24\",\"v25\",\"v26\",\"v27\",\"v28\",\"amount\") " +
				"VALUES (" + String.join(",", record) + ")");
	}

	public void createCreditCardTransactionDatabase(boolean recreateTable) {
		this.jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS \"cdc\"");
		this.jdbcTemplate.execute("SET search_path TO \"cdc\"");

		if (recreateTable) {
			try {
				this.jdbcTemplate.execute("DROP TABLE IF EXISTS \"cdc\".\"credit_card_transaction\"");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS \"cdc\".\"credit_card_transaction\" (" +
				"    \"time\"   FLOAT," +
				"    \"v1\"     FLOAT," +
				"    \"v2\"     FLOAT," +
				"    \"v3\"     FLOAT," +
				"    \"v4\"     FLOAT," +
				"    \"v5\"     FLOAT," +
				"    \"v6\"     FLOAT," +
				"    \"v7\"     FLOAT," +
				"    \"v8\"     FLOAT," +
				"    \"v9\"     FLOAT," +
				"    \"v10\"    FLOAT," +
				"    \"v11\"    FLOAT," +
				"    \"v12\"    FLOAT," +
				"    \"v13\"    FLOAT," +
				"    \"v14\"    FLOAT," +
				"    \"v15\"    FLOAT," +
				"    \"v16\"    FLOAT," +
				"    \"v17\"    FLOAT," +
				"    \"v18\"    FLOAT," +
				"    \"v19\"    FLOAT," +
				"    \"v20\"    FLOAT," +
				"    \"v21\"    FLOAT," +
				"    \"v22\"    FLOAT," +
				"    \"v23\"    FLOAT," +
				"    \"v24\"    FLOAT," +
				"    \"v25\"    FLOAT," +
				"    \"v26\"    FLOAT," +
				"    \"v27\"    FLOAT," +
				"    \"v28\"    FLOAT," +
				"    \"amount\" FLOAT " +
				")");
	}
}
