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

package org.springframework.cloud.stream.app.processor.fraud.detection;

/**
 * @author Christian Tzolov
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tensorflow.Tensor;
import org.tensorflow.Tensors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.fn.common.tensorflow.GraphRunner;
import org.springframework.cloud.fn.common.tensorflow.ProtoBufGraphDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

/**
 * @author Christian Tzolov
 */
@Configuration
@EnableConfigurationProperties(FraudDetectionProcessorProperties.class)
public class FraudDetectionProcessorConfiguration {


	@Bean
	@ConditionalOnMissingBean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public Function<Message<byte[]>, Message<byte[]>> fraudDetectionFunction(
			FraudDetectionProcessorProperties properties,
			Function<byte[], Map<String, Tensor<?>>> payloadToTensorMap,
			Function<Map<String, Tensor<?>>, String> fromTensorMap) {

		return input -> {
			byte[] payload = input.getPayload();

			GraphRunner graphRunner = new GraphRunner(Arrays.asList("pkeep", "input"), "output")
					.withGraphDefinition(new ProtoBufGraphDefinition(properties.getTensorflow().getModelUri(), true));

			String result = payloadToTensorMap
					.andThen(graphRunner)
					.andThen(fromTensorMap)
					.apply(payload);

			Message<byte[]> outMessage = MessageBuilder
					.withPayload(result.getBytes(StandardCharsets.UTF_8))
					//.setHeader(SEMANTIC_SEGMENTATION_HEADER, jsonMaskPixels)
					.build();

			return outMessage;
		};
	}

	@Bean
	public Function<byte[], Map<String, Tensor<?>>> payloadToTensorMap(ObjectMapper mapper) {
		return payload -> {

			Map<String, Number> cdcEntryMap = null;
			try {
				cdcEntryMap = mapper.readValue(payload, Map.class);
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			Assert.notNull(cdcEntryMap, "Failed to parse the cdcEntryMap!");

			Assert.isTrue(cdcEntryMap.size() == CdcEntryNormalizationUtils.columns.length,
					"Invalid number of fields in CdcEntryMap:" + cdcEntryMap.size());

			float[] rawInput = new float[CdcEntryNormalizationUtils.columns.length - 1];

			for (int i = 0; i < CdcEntryNormalizationUtils.columns.length - 1; i++) {
				Number v = cdcEntryMap.get(CdcEntryNormalizationUtils.columns[i]);
				rawInput[i] = v.floatValue();
			}

			float[][] normalizedInput = CdcEntryNormalizationUtils.normalize(rawInput);

			Map<String, Tensor<?>> response = new HashMap<>();
			response.put("pkeep", Tensors.create(1.0f));
			response.put("input", Tensors.create(normalizedInput));

			return response;
		};
	}

	@Bean
	public Function<Map<String, Tensor<?>>, String> fromTensorMap(ObjectMapper mapper) {
		return resultTensors -> {
			float[][] val = resultTensors.get("output").copyTo(new float[1][2]);

			String result = (val[0][1] < 0.999 && val[0][0] >= 0.999) ? "FRAUD" : "NORMAL";
			Map<String, Object> outputJsonMap = new HashMap<>();
			outputJsonMap.put("detection", result);

			try {
				return mapper.writeValueAsString(outputJsonMap);
			}
			catch (JsonProcessingException e) {
				throw new RuntimeException("Failed to generate JSON output", e);
			}
		};
	}
}
