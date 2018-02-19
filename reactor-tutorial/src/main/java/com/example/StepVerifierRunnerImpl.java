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

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class StepVerifierRunnerImpl implements StepVerifierRunner {

    @Override
    public void verifyFooBar(final Flux<String> flux) {
        StepVerifier.create(flux)
                .expectNext("foo")
                .expectNext("bar")
                .verifyComplete();
    }

    @Override
    public void verifyFooBarThenException(final Flux<String> flux) {
        StepVerifier.create(flux)
                .expectNext("foo", "bar")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Override
    public void verifyUsername(final Flux<User> flux) {
        StepVerifier.create(flux)
                .assertNext(user -> assertThat(user.getUsername()).isEqualTo("scott"))
                .assertNext(user -> assertThat(user.getUsername()).isEqualTo("tiger"))
                .verifyComplete();
    }

    @Override
    public void verify10Items(final Flux<Integer> flux) {
        
    }
}
