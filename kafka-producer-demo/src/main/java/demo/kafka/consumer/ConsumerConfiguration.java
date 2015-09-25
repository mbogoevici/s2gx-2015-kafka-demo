/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package demo.kafka.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.kafka.Kafka;
import org.springframework.integration.dsl.kafka.KafkaMessageDrivenChannelAdapterSpec;
import org.springframework.integration.kafka.core.DefaultConnectionFactory;
import org.springframework.integration.kafka.core.ZookeeperConfiguration;
import org.springframework.integration.kafka.listener.KafkaTopicOffsetManager;
import org.springframework.integration.kafka.serializer.common.StringDecoder;
import org.springframework.integration.kafka.support.ZookeeperConnect;

/**
 * @author Marius Bogoevici
 */
@EnableIntegration
@Configuration
public class ConsumerConfiguration {

	@Bean
	IntegrationFlow consumer() {
		KafkaMessageDrivenChannelAdapterSpec kafkaMessageDrivenChannelAdapterSpec  =
				Kafka.messageDriverChannelAdapter(new DefaultConnectionFactory(new ZookeeperConfiguration("localhost:2181")), "s2gx")
						.configureListenerContainer(kafkaMessageListenerContainerSpec ->  {
							kafkaMessageListenerContainerSpec.offsetManager(offsetManager());
						});
		kafkaMessageDrivenChannelAdapterSpec.payloadDecoder(new StringDecoder());
		return IntegrationFlows.from(kafkaMessageDrivenChannelAdapterSpec).handle(m -> {
			System.out.println(m.getHeaders());
			System.out.println(m.getPayload());
		}).get();
	}

	@Bean
	public KafkaTopicOffsetManager offsetManager() {
		return new KafkaTopicOffsetManager(new ZookeeperConnect("localhost:2181"), "springXDtopic");
	}
}
