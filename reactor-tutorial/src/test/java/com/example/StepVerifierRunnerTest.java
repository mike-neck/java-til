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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTimeout;

@ExtendWith({ParameterSupplier.class})
class StepVerifierRunnerTest {

    private CountDownLatch countDownLatch;

    @BeforeEach
    void setupLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    @Test
    void fooBarTesting(final StepVerifierRunner stepVerifierRunner) {
        stepVerifierRunner.verifyFooBar(Flux.just("foo", "bar").doOnComplete(countDownLatch::countDown));
        assertTimeout(Duration.ofMillis(500L), () -> countDownLatch.await(2000L, TimeUnit.MILLISECONDS));
    }

    @Test
    void fooBarThenExceptionTesting(final StepVerifierRunner stepVerifierRunner) {
        final Stream<String> stream = Stream.concat(Stream.of("foo", "bar"), Stream.generate(() -> {
            countDownLatch.countDown();
            throw new RuntimeException("foo-bar-exception");
        }));
        final Flux<String> flux = Flux.fromStream(stream);
        stepVerifierRunner.verifyFooBarThenException(flux);
        assertTimeout(Duration.ofMillis(500L), () -> countDownLatch.await(2000L, TimeUnit.MILLISECONDS));
    }
}
