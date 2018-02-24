/*
 * Copyright 2018 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;


import com.example.annotations.Lesson;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import javax.inject.Inject;

@Lesson(6)
public class SubscribeWithStepVerifierImpl implements SubscribeWithStepVerifier {

    private final SubscribeWithStepVerifier subscribe;

    @Inject
    public SubscribeWithStepVerifierImpl(final SubscribeWithStepVerifier subscribe) {
        this.subscribe = subscribe;
    }

    @Override
    public StepVerifier requestAll(final Flux<Long> flux) {
        return StepVerifier.create(flux).expectSubscription().expectNextCount(4L).expectComplete();
    }

    @Override
    public StepVerifier req1FooReq2BarBaz(final Flux<String> flux) {
        return null;
    }
}
