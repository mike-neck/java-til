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
package com.example.lesson;

import com.example.ParameterSupplier;
import com.example.lesson.api.StepVerifierRunner;
import com.example.User;
import com.example.annotations.Lesson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@Lesson(3)
@ExtendWith({ParameterSupplier.class})
class StepVerifierRunnerTest {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    @AfterAll
    static void stop() {
        EXECUTOR.shutdown();
    }

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

    @Test
    void assertionForUsersName(final StepVerifierRunner stepVerifierRunner) {
        final Flux<User> flux = Flux.<User>just(() -> () -> "scott", () -> () -> "tiger").doOnComplete(countDownLatch::countDown);
        stepVerifierRunner.verifyUsername(flux);
        assertTimeout(Duration.ofMillis(500L), () -> countDownLatch.await(2000L, TimeUnit.MILLISECONDS));
    }

    @Test
    void verify10Items(final StepVerifierRunner stepVerifierRunner) {
        final Stream<Integer> stream = IntStream.iterate(0, i -> i + 1).limit(10L).boxed();
        stepVerifierRunner.verify10Items(Flux.fromStream(() -> stream).doOnComplete(countDownLatch::countDown));
        assertTimeout(Duration.ofMillis(500L), () -> countDownLatch.await(2000L, TimeUnit.MILLISECONDS));
    }

    @Test
    void verifyTooLongFlux(final StepVerifierRunner stepVerifierRunner) throws InterruptedException {
        final Supplier<Flux<Long>> flux = () -> Flux.interval(Duration.ofSeconds(1L), Duration.ofSeconds(1L)).take(3600L)
                .log("too-long-test")
                .doOnComplete(countDownLatch::countDown);
        final Future<?> future = EXECUTOR.submit(() -> stepVerifierRunner.verifyTooLongFlux(flux));
        Thread.sleep(2_000L);
        future.cancel(true);
        assertEquals(0, countDownLatch.getCount());
    }
}
