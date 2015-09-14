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

package demo.kafka.producer;


import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.kafka.Kafka;
import org.springframework.integration.dsl.kafka.KafkaProducerMessageHandlerSpec;
import org.springframework.integration.kafka.support.KafkaHeaders;
import org.springframework.integration.kafka.support.ProducerMetadata;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author Marius Bogoevici
 */
@EnableIntegration
@Configuration
public class ProducerConfiguration {

	public static final StringSerializer STRING_SERIALIZER = new StringSerializer();

	@Bean
	IntegrationFlow producer() {
		KafkaProducerMessageHandlerSpec kafkaProducerMessageHandlerSpec  =
				Kafka.outboundChannelAdapter().addProducer(
						new ProducerMetadata<>("event-bus", String.class, String.class,
								STRING_SERIALIZER, STRING_SERIALIZER), "localhost:9092");
		return flow -> flow.handle(kafkaProducerMessageHandlerSpec);
	}

	@Bean
	CommandLineRunner run(@Qualifier("producer.input")MessageChannel messageChannel) {
		return args -> messageChannel.send(MessageBuilder.withPayload("Hi")
				.setHeader(KafkaHeaders.TOPIC, "another-topic").build());
	}
}
