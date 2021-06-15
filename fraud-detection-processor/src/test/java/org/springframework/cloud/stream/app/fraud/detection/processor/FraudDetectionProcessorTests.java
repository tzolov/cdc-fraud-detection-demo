/*
 * Copyright 2020-2020 the original author or authors.
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

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.app.processor.fraud.detection.FraudDetectionProcessorConfiguration;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Christian Tzolov
 */
public class FraudDetectionProcessorTests {

	@Test
	public void testNormalTransaction() {
		byte[] payload = ("{\"time\":403, \"v1\":1.13154130503628, \"v2\":-0.174361326933011, \"v3\":0.992621499569349, " +
				"\"v4\":0.600032005318704, \"v5\":-1.02193420532219, \"v6\":-0.644778975272613, \"v7\":-0.428619436868548, " +
				"\"v8\":-0.032070612607315, \"v9\":0.516317802598357, \"v10\":-0.118985161287334, \"v11\":-0.366030394703643, " +
				"\"v12\":0.116521247694258, \"v13\":0.0519083537903152, \"v14\":0.0143908407311333, \"v15\":1.42491667851833, " +
				"\"v16\":0.672573972704233, \"v17\":-0.563922580691629, \"v18\":0.0316975656460402, \"v19\":-0.385113763680896, " +
				"\"v20\":0.00661885129172743, \"v21\":0.0220683458725804, \"v22\":-0.0311494841173491, \"v23\":0.0228454809156195, " +
				"\"v24\":0.41459099518237, \"v25\":0.129934943366392, \"v26\":0.387227990992455, \"v27\":-0.016423479187404, " +
				"\"v28\":0.034254726765817, \"amount\":52.4}").getBytes();

		testTransaction(payload, "NORMAL");
	}

	@Test
	public void testFraudTransaction() {

		byte[] payload = ("{\"time\":406, \"v1\":-2.3122265423263, \"v2\":1.95199201064158, \"v3\":-1.60985073229769, " +
				"\"v4\":3.9979055875468, \"v5\":-0.522187864667764, \"v6\":-1.42654531920595, \"v7\":-2.53738730624579, " +
				"\"v8\":1.39165724829804, \"v9\":-2.77008927719433, \"v10\":-2.77227214465915, \"v11\":3.20203320709635, " +
				"\"v12\":-2.89990738849473, \"v13\":-0.595221881324605, \"v14\":-4.28925378244217, \"v15\":0.389724120274487, " +
				"\"v16\":-1.14074717980657, \"v17\":-2.83005567450437, \"v18\":-0.0168224681808257, \"v19\":0.416955705037907, " +
				"\"v20\":0.126910559061474, \"v21\":0.517232370861764, \"v22\":-0.0350493686052974, \"v23\":-0.465211076182388, " +
				"\"v24\":0.320198198514526, \"v25\":0.0445191674731724, \"v26\":0.177839798284401, \"v27\":0.261145002567677, " +
				"\"v28\":-0.143275874698919, \"amount\":0}").getBytes();

		testTransaction(payload, "FRAUD");
	}

	private void testTransaction(byte[] payload, String expectedState) {

		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
				TestChannelBinderConfiguration.getCompleteConfiguration(FraudDetectionTestApplication.class))
				.web(WebApplicationType.NONE)
				.run("--spring.cloud.function.definition=fraudDetectionFunction")) {

			InputDestination processorInput = context.getBean(InputDestination.class);
			OutputDestination processorOutput = context.getBean(OutputDestination.class);

			processorInput.send(new GenericMessage<>(payload));
			Message<byte[]> received = processorOutput.receive(10000);

			assertThat(received).isNotNull();
			assertThat(received.getPayload()).isEqualTo(("{\"detection\":\"" + expectedState + "\"}").getBytes(StandardCharsets.UTF_8));
		}
	}

	@SpringBootApplication
	@Import(FraudDetectionProcessorConfiguration.class)
	public static class FraudDetectionTestApplication {
	}

}
