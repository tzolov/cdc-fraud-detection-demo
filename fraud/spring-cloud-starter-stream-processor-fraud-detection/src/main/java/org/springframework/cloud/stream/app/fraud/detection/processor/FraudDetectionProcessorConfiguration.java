/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.fraud.detection.processor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tensorflow.Tensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.app.tensorflow.processor.DefaultOutputMessageBuilder;
import org.springframework.cloud.stream.app.tensorflow.processor.OutputMessageBuilder;
import org.springframework.cloud.stream.app.tensorflow.processor.TensorflowCommonProcessorConfiguration;
import org.springframework.cloud.stream.app.tensorflow.processor.TensorflowCommonProcessorProperties;
import org.springframework.cloud.stream.app.tensorflow.processor.TensorflowInputConverter;
import org.springframework.cloud.stream.app.tensorflow.processor.TensorflowOutputConverter;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

/**
 *
 * @author Christian Tzolov
 */
@Configuration
@EnableBinding(Processor.class)
@EnableConfigurationProperties({ FraudDetectionProcessorProperties.class, TensorflowCommonProcessorProperties.class })
@Import(TensorflowCommonProcessorConfiguration.class)
public class FraudDetectionProcessorConfiguration {

	private static final Log logger = LogFactory.getLog(FraudDetectionProcessorConfiguration.class);

	@Autowired
	private TensorflowCommonProcessorProperties commonProcessorProperties;

	@Autowired
	private FraudDetectionProcessorProperties properties;

	@Bean
	public OutputMessageBuilder tensorflowOutputMessageBuilder() {
		return new DefaultOutputMessageBuilder(commonProcessorProperties);
	}

	@Bean
	public TensorflowOutputConverter tensorflowOutputConverter() {
		Assert.notNull(this.commonProcessorProperties.getModelFetch(), "Fetch outputs are null");
		Assert.isTrue(this.commonProcessorProperties.getModelFetch().size() == 1,
				"A single fetch output is expected but found:" + this.commonProcessorProperties.getModelFetch());

		return new FraudOutputConverter(this.commonProcessorProperties.getModelFetch().get(0));
	}

	@Bean
	public TensorflowInputConverter tensorflowInputConverter() {
		return new FraudInputConverter();
	}

	public static class FraudOutputConverter implements TensorflowOutputConverter<String> {
		private String fetchOutput;

		public FraudOutputConverter(String fetchOutput) {
			this.fetchOutput = fetchOutput;
		}

		@Override
		public String convert(Map<String, Tensor<?>> resultTensors, Map<String, Object> processorContext) {

			float[][] val = resultTensors.get(fetchOutput).copyTo(new float[1][2]);

			String result = (val[0][1] < 0.999 && val[0][0] >= 0.999) ? "FRAUD" : "NORMAL";
			Map<String, Object> outputJsonMap = new HashMap<>();
			outputJsonMap.put("detection", result);

			try {
				return new ObjectMapper().writeValueAsString(outputJsonMap);
			}
			catch (JsonProcessingException e) {
				throw new RuntimeException("Failed to generate JSON output", e);
			}

		}
	}

	public static class FraudInputConverter implements TensorflowInputConverter {

		@Override
		public Map<String, Object> convert(Object input, Map<String, Object> processorContext) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				Map<String, Number> cdcEntryMap = null;

				if (input instanceof byte[]) {
					cdcEntryMap = objectMapper.readValue((byte[]) input, Map.class);
				}
				else if (input instanceof String) {
					cdcEntryMap = objectMapper.readValue((String) input, Map.class);
				}
				else if (input instanceof Map) {
					cdcEntryMap = (Map) input;
				}

				if (cdcEntryMap != null) {
					//processorContext.put(PROCESSOR_CONTEXT_INPUT_RECORD, new String((byte[]) input));
					return getStringObjectMap(cdcEntryMap);
				}

				throw new IllegalArgumentException("Unsupported payload type:" + input);
			}
			catch (IOException e) {
				throw new RuntimeException("Can't parse input tweet json: " + input);
			}

		}

		private Map<String, Object> getStringObjectMap(Map<String, Number> cdcEntryMap) {
			Assert.notNull(cdcEntryMap, "Failed to parse the cdcEntryMap!");

			Assert.isTrue(cdcEntryMap.size() == CdcEntryNormalizationUtils.columns.length,
					"Invalid number of fields in CdcEntryMap:" + cdcEntryMap.size());

			float[] rawInput = new float[CdcEntryNormalizationUtils.columns.length - 1];

			for (int i = 0; i < CdcEntryNormalizationUtils.columns.length - 1; i++) {
				Number v = cdcEntryMap.get(CdcEntryNormalizationUtils.columns[i]);
				rawInput[i] = v.floatValue();
			}

			float[][] normalizedInput = CdcEntryNormalizationUtils.normalize(rawInput);

			Map<String, Object> response = new HashMap<>();
			response.put("pkeep", 1.0f);
			response.put("input", normalizedInput);

			return response;
		}
	}
}
