/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.fraud.detection.processor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Christian Tzolov
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = {
				"debug=false",
				"logging.level.*=INFO",
				//"tensorflow.model=classpath:/fraud_detection_graph.pb",
				//"tensorflow.modelFetch=output",
		})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class FraudDetectionProcessorIntegrationTests {

	@Autowired
	protected Processor channels;

	@Autowired
	protected MessageCollector messageCollector;

	public static class FraudDetectionPayloadTests extends FraudDetectionProcessorIntegrationTests {

		@Test
		public void testNormalTransaction() {

			Object payloadNormal = ("{\"time\":403, \"v1\":1.13154130503628, \"v2\":-0.174361326933011, \"v3\":0.992621499569349, " +
					"\"v4\":0.600032005318704, \"v5\":-1.02193420532219, \"v6\":-0.644778975272613, \"v7\":-0.428619436868548, " +
					"\"v8\":-0.032070612607315, \"v9\":0.516317802598357, \"v10\":-0.118985161287334, \"v11\":-0.366030394703643, " +
					"\"v12\":0.116521247694258, \"v13\":0.0519083537903152, \"v14\":0.0143908407311333, \"v15\":1.42491667851833, " +
					"\"v16\":0.672573972704233, \"v17\":-0.563922580691629, \"v18\":0.0316975656460402, \"v19\":-0.385113763680896, " +
					"\"v20\":0.00661885129172743, \"v21\":0.0220683458725804, \"v22\":-0.0311494841173491, \"v23\":0.0228454809156195, " +
					"\"v24\":0.41459099518237, \"v25\":0.129934943366392, \"v26\":0.387227990992455, \"v27\":-0.016423479187404, " +
					"\"v28\":0.034254726765817, \"amount\":52.4}").getBytes();


			channels.input().send(MessageBuilder.withPayload(payloadNormal).build());

			Message<?> received = messageCollector.forChannel(channels.output()).poll();

			Assert.assertNotNull(received);
			Assert.assertEquals("{\"detection\":\"NORMAL\"}", received.getPayload());
		}

		@Test
		public void testFraudTransaction() {

			Object payloadFraud = ("{\"time\":406, \"v1\":-2.3122265423263, \"v2\":1.95199201064158, \"v3\":-1.60985073229769, " +
					"\"v4\":3.9979055875468, \"v5\":-0.522187864667764, \"v6\":-1.42654531920595, \"v7\":-2.53738730624579, " +
					"\"v8\":1.39165724829804, \"v9\":-2.77008927719433, \"v10\":-2.77227214465915, \"v11\":3.20203320709635, " +
					"\"v12\":-2.89990738849473, \"v13\":-0.595221881324605, \"v14\":-4.28925378244217, \"v15\":0.389724120274487, " +
					"\"v16\":-1.14074717980657, \"v17\":-2.83005567450437, \"v18\":-0.0168224681808257, \"v19\":0.416955705037907, " +
					"\"v20\":0.126910559061474, \"v21\":0.517232370861764, \"v22\":-0.0350493686052974, \"v23\":-0.465211076182388, " +
					"\"v24\":0.320198198514526, \"v25\":0.0445191674731724, \"v26\":0.177839798284401, \"v27\":0.261145002567677, " +
					"\"v28\":-0.143275874698919, \"amount\":0}").getBytes();

			channels.input().send(MessageBuilder.withPayload(payloadFraud).build());

			Message<?> received = messageCollector.forChannel(channels.output()).poll();

			Assert.assertNotNull(received);
			Assert.assertEquals("{\"detection\":\"FRAUD\"}", received.getPayload());
		}
	}

	@SpringBootConfiguration
	@EnableAutoConfiguration
	@Import(FraudDetectionProcessorConfiguration.class)
	public static class TestFraudDetectionProcessorApplication {

	}

}
