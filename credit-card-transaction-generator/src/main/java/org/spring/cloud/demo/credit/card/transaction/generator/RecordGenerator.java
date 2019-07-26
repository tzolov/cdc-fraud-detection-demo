package org.spring.cloud.demo.credit.card.transaction.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Christian Tzolov
 */
@Component
public class RecordGenerator {

	public static String[] columns = new String[] { "Time", "V1", "V2", "V3", "V4", "V5", "V6", "V7", "V8", "V9", "V10",
			"V11", "V12", "V13", "V14", "V15", "V16", "V17", "V18", "V19", "V20", "V21", "V22", "V23", "V24", "V25",
			"V26", "V27", "V28", "Amount", "Class" };

	private final List<String[]> normalRecords;
	private final List<String[]> fraudRecords;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private RecordGeneratorProperties properties;

	private ExecutorService executorService;

	public RecordGenerator() {
		this.executorService = Executors.newSingleThreadExecutor();
		this.normalRecords = readCsv("classpath:/data/credit-card-normal.csv");
		this.fraudRecords = readCsv("classpath:/data/credit-card-fraud.csv");
	}

	private List<String[]> readCsv(String csvFilePath) {
		try {
			Resource csvResource = new DefaultResourceLoader().getResource(csvFilePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(csvResource.getInputStream()));

			List<String[]> list = new ArrayList<>();

			while (reader.ready()) {
				String[] fieldStr = reader.readLine().split(",");
				Arrays.copyOf(fieldStr, fieldStr.length - 1); // Drop the Class column
				list.add(Arrays.copyOf(fieldStr, fieldStr.length - 1));
			}
			reader.close();
			return list;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PostConstruct
	public void initDatabase() {
		if (this.properties.isDropSchema()) {
			String dropSchemaScript = getResourceAsString("classpath:/drop-schema.sql");
			Assert.isTrue(!StringUtils.isEmpty(dropSchemaScript), "Failed to retrieve the drop-schema.sql");
			this.jdbcTemplate.execute(dropSchemaScript);
		}

		// (re)create the DB schema if missing
		String createSchemaScript = getResourceAsString("classpath:/create-schema.sql");
		Assert.isTrue(!StringUtils.isEmpty(createSchemaScript), "Failed to retrieve the create-schema.sql");
		this.jdbcTemplate.execute(createSchemaScript);
	}

	private String getResourceAsString(String uri) {
		try {
			return IOUtils.toString(new DefaultResourceLoader().getResource(uri).getInputStream(), Charset.forName("UTF8"));
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to retrieve resource from URI:" + uri, e);
		}
	}

	public void start(GeneratorControlView mainView) {

		this.executorService.submit(() -> {
			try {
				Random random = new Random();
				while (mainView.isStopped() == false) {
					if (random.nextInt(100) >= mainView.getFraudPercentage()) {
						insertTransaction(normalRecords.get(random.nextInt(normalRecords.size())));
					}
					else {
						insertTransaction(fraudRecords.get(random.nextInt(fraudRecords.size())));
					}

					Thread.sleep(1000 * (mainView.getMinWaitSecond() +
							random.nextInt((int) mainView.getMaxWaitSecond())));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void insertTransaction(String... record) {
		Assert.isTrue(record.length == columns.length - 1,
				"Incorrect number of columns in record: " + record.length);
		jdbcTemplate.execute("INSERT INTO \"cdc\".\"credit_card_transaction\" (\"time\",\"v1\",\"v2\",\"v3\",\"v4\",\"v5\"," +
				"\"v6\",\"v7\",\"v8\",\"v9\",\"v10\",\"v11\",\"v12\",\"v13\",\"v14\",\"v15\",\"v16\",\"v17\",\"v18\"" +
				",\"v19\",\"v20\",\"v21\",\"v22\",\"v23\",\"v24\",\"v25\",\"v26\",\"v27\",\"v28\",\"amount\") " +
				"VALUES (" + String.join(",", record) + ")");
	}
}
