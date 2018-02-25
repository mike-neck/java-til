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

import com.example.lesson.api.Merger;
import com.example.ParameterSupplier;
import com.example.annotations.Lesson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@Lesson(5)
@ExtendWith({ParameterSupplier.class})
class MergingTest {

    @Test
    void mergingFlux(final Merger merger) {
        final Flux<Long> delayed = Flux.defer(() -> Flux.interval(Duration.ofSeconds(5L), Duration.ofSeconds(7L)).log("delayed").take(2L));
        final Flux<Long> notDelayed = Flux.defer(() -> Flux.interval(Duration.ofSeconds(3L)).map(value -> value + 100).log("not-delayed").take(3L));

        StepVerifier.withVirtualTime(() -> merger.mergeFlux(delayed, notDelayed))
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3L))
                .expectNext(100L)
                .thenAwait(Duration.ofSeconds(2L))
                .expectNext(0L)
                .thenAwait(Duration.ofSeconds(1L))
                .expectNext(101L)
                .thenAwait(Duration.ofSeconds(3L))
                .expectNext(102L)
                .thenAwait(Duration.ofSeconds(3L))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void concatFlux(final Merger merger) {
        final Flux<Long> delayed = Flux.defer(() -> Flux.interval(Duration.ofSeconds(5L), Duration.ofSeconds(7L)).log("delayed").take(2L));
        final Flux<Long> notDelayed = Flux.defer(() -> Flux.interval(Duration.ofSeconds(3L)).map(value -> value + 100).log("not-delayed").take(3L));

        StepVerifier.withVirtualTime(() -> merger.mergeFromLeftKeepingOrderFromLeft(delayed, notDelayed))
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(5L))
                .expectNext(0L)
                .thenAwait(Duration.ofSeconds(7L))
                .expectNext(1L)
                .thenAwait(Duration.ofSeconds(3L))
                .expectNext(100L)
                .thenAwait(Duration.ofSeconds(6L))
                .expectNext(101L, 102L)
                .verifyComplete();
    }

    @Test
    void concatMono(final Merger merger) {
        final Mono<Long> delayed = Mono.defer(() -> Mono.delay(Duration.ofSeconds(3L)).map(value -> value + 100));
        final Mono<Long> notDelayed = Mono.defer(() -> Mono.delay(Duration.ofSeconds(1L)));

        StepVerifier.withVirtualTime(() -> merger.mergeFromLeftKeepingOrderFromLeft(delayed, notDelayed))
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(3L))
                .expectNext(100L)
                .expectNoEvent(Duration.ofSeconds(1L))
                .expectNext(0L)
                .verifyComplete();
    }
}
