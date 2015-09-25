/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package s2gx2015.demo.spring.cloud.stream.rxjava.connectors.rxjava;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.integration.handler.AbstractMessageProducingHandler;
import org.springframework.messaging.Message;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ObservableMessageHandler<T> extends AbstractMessageProducingHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Subject<Object,T> subject;

	@SuppressWarnings({"unchecked", "rawtypes"})
	public ObservableMessageHandler() {
		subject = new SerializedSubject(PublishSubject.create());
	}

	public Observable<T> getObservable() {
		return subject;
	}

	@Override
	//todo: support module input type
	protected void handleMessageInternal(Message<?> message) throws Exception {
		subject.onNext(message.getPayload());
	}

}
