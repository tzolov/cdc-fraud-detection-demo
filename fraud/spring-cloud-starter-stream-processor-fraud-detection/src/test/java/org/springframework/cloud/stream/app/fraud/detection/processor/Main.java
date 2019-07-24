package org.springframework.cloud.stream.app.fraud.detection.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**
 * @author Christian Tzolov
 */
public class Main {


	public static void main(String[] args) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Resource jsonResource = new DefaultResourceLoader().getResource("classpath:/data/creditcard.json");
		BufferedReader reader = new BufferedReader(new InputStreamReader(jsonResource.getInputStream()));
		while (reader.ready()) {
			String jsonLine = reader.readLine();
			Map<String, Float> v = mapper.readValue(jsonLine, Map.class);
			System.out.println(v);
		}
	}
}
