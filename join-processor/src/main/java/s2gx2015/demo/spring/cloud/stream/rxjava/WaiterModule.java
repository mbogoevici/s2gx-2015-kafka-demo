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

package s2gx2015.demo.spring.cloud.stream.rxjava;

import java.util.concurrent.TimeUnit;

import rx.Observable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.Bindings;
import org.springframework.cloud.stream.annotation.EnableBinding;
import s2gx2015.demo.spring.cloud.stream.rxjava.connectors.rxjava.ObservableMessageHandler;
import s2gx2015.demo.spring.cloud.stream.rxjava.connectors.rxjava.ObservableMessageProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * @author Marius Bogoevici
 */
@EnableBinding(Waiter.class)
public class WaiterModule {

	@Autowired @Bindings(WaiterModule.class)
	Waiter waiter;

	@Bean
	@ServiceActivator(inputChannel = "hotDrinks")
	public ObservableMessageHandler<byte[]> hotDrinksObservable() {
		return new ObservableMessageHandler<>();
	}

	@Bean
	@ServiceActivator(inputChannel = "coldDrinks")
	public ObservableMessageHandler<byte[]> coldDrinksObservable() {
		return new ObservableMessageHandler<>();
	}

	@Bean
	@SuppressWarnings("unchecked")
	public ObservableMessageProducer mergedObservable(Waiter waiter) {

		Observable hotDrinks = hotDrinksObservable().getObservable().cast(byte[].class).map(String::new);

		Observable coldDrinks = coldDrinksObservable().getObservable().cast(byte[].class).map(String::new);

		Observable<Observable<byte[]>> join = hotDrinks.join(
				coldDrinks,
				s -> Observable.timer(5, TimeUnit.SECONDS),
				Observable::just,
				(hotDrink, coldDrink) ->
						(hotDrink.toString() + coldDrink.toString()).getBytes());

		return new ObservableMessageProducer(join, waiter.deliveries());
	}
}
