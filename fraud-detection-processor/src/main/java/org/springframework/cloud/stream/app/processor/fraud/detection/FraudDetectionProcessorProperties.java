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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * @author Christian Tzolov
 */
@ConfigurationProperties("fraud.detection.processor")
@Validated
public class FraudDetectionProcessorProperties {

	/**
	 * Pre-trained (frozen) Tensorflow model URI. The http://, file:// and classpath:// URI schemas are supported.
	 */
	private String model = "classpath:/fraud_detection_graph.pb";

	/**
	 * When enabled keeps a local copy (cache) of the model (protobuf) files extracted from the URI archive.
	 */
	private boolean modelCached = true;

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public boolean isModelCached() {
		return modelCached;
	}

	public void setModelCached(boolean modelCached) {
		this.modelCached = modelCached;
	}
}
