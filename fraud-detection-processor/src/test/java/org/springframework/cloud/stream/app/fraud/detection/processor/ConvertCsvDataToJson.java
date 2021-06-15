/*
 * Copyright 2021-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.fraud.detection.processor;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**
 * @author Christian Tzolov
 */
public final class ConvertCsvDataToJson {

	private ConvertCsvDataToJson() {

	}

	public static String[] columns = new String[] { "Time", "V1", "V2", "V3", "V4", "V5", "V6", "V7", "V8", "V9", "V10",
			"V11", "V12", "V13", "V14", "V15", "V16", "V17", "V18", "V19", "V20", "V21", "V22", "V23", "V24", "V25",
			"V26", "V27", "V28", "Amount", "Class" };

	public static void main(String[] args) throws IOException {
		convertCsvToJsonFile("classpath:/data/creditcard.csv");
	}

	private static void convertCsvToJsonFile(String csvFilePath) throws IOException {
		Resource csvResource = new DefaultResourceLoader().getResource(csvFilePath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(csvResource.getInputStream()));

		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream("./spring-cloud-starter-stream-processor-fraud-detection/src/test/resources/data/creditcard.json"));

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
}
