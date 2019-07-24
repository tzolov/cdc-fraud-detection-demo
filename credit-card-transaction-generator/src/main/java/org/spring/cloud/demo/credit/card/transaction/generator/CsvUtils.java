package org.spring.cloud.demo.credit.card.transaction.generator;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**
 * @author Christian Tzolov
 */
public class CsvUtils {

	public static String[] columns = new String[] { "Time", "V1", "V2", "V3", "V4", "V5", "V6", "V7", "V8", "V9", "V10",
			"V11", "V12", "V13", "V14", "V15", "V16", "V17", "V18", "V19", "V20", "V21", "V22", "V23", "V24", "V25",
			"V26", "V27", "V28", "Amount", "Class" };

	public static int CLASS_INDEX = columns.length - 1;

	public static void main(String[] args) throws IOException {
		//convertCsvToJsonFile("classpath:/data/creditcard.csv");
		extractFraudRecords("classpath:/data/creditcard.csv");
	}

	private static void convertCsvToJsonFile(String csvFilePath) throws IOException {
		Resource csvResource = new DefaultResourceLoader().getResource(csvFilePath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(csvResource.getInputStream()));

		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream("./spring-cloud-starter-stream-processor-fraud-detection/src/main/resources/data/creditcard.json"));

		reader.readLine(); //header

		int cnt = 0;
		while (reader.ready()) {
			cnt++;
			String line = reader.readLine();
			String[] fieldStr = line.split(",");
			String jsonLine = "";
			for (int i = 0; i < columns.length; i++) {
				jsonLine = jsonLine + "\"" + columns[i] + "\":" + fieldStr[i];
				if (i != columns.length - 1) {
					jsonLine = jsonLine + ", ";
				}
			}
			jsonLine = "{" + jsonLine + "}";

			IOUtils.write(jsonLine + "\n", bos, Charset.forName("UTF-8"));

			if (cnt % 1000 == 0) {
				System.out.print(" : " + cnt);
			}
		}
		bos.flush();
		bos.close();
	}

	public static List<String[]> readCsv(String csvFilePath, boolean dropHeader) throws IOException {
		Resource csvResource = new DefaultResourceLoader().getResource(csvFilePath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(csvResource.getInputStream()));

		if (dropHeader) {
			reader.readLine(); //header
		}

		List<String[]> list = new ArrayList<>();

		while (reader.ready()) {
			String[] fieldStr = reader.readLine().split(",");
			Arrays.copyOf(fieldStr, fieldStr.length - 1);
			list.add(Arrays.copyOf(fieldStr, fieldStr.length - 1));
		}
		return list;
	}

	private static void extractFraudRecords(String csvFilePath) throws IOException {
		Resource csvResource = new DefaultResourceLoader().getResource(csvFilePath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(csvResource.getInputStream()));

		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream("/Users/ctzolov/Dev/projects/credit-card-transaction-generator/src/main/resources/data/creditcard-normal.csv"));

		reader.readLine(); //header

		int cnt = 0;
		while (reader.ready()) {
			cnt++;
			String line = reader.readLine();

			if (line.split(",")[CLASS_INDEX].contains("0")) {
				IOUtils.write(line + "\n", bos, Charset.forName("UTF-8"));
			}


			if (cnt % 1000 == 0) {
				System.out.print(" : " + cnt);
			}
		}
		bos.flush();
		bos.close();
	}
}
