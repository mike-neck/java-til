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
import com.example.annotations.Lesson;
import com.example.lesson.api.Operations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;

@Lesson(9)
@ExtendWith({ParameterSupplier.class})
class OperationTest {

    @Test
    void zippingIntMonthIntToLocalDate(final Operations operations) {
        final Flux<Integer> yearFlux = Flux.just(2018, 2018, 2017, 2018);
        final Flux<Month> monthFlux = Flux.just(Month.JANUARY, Month.MARCH, Month.DECEMBER, Month.JANUARY);
        final Flux<Integer> dayOfMonthFlux = Flux.just(2, 30, 31, 17);

        final Flux<LocalDate> flux = operations.zipFluxToLocalDate(yearFlux, monthFlux, dayOfMonthFlux);

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(LocalDate.of(2018, Month.JANUARY, 2))
                .expectNext(LocalDate.of(2018, Month.MARCH, 30))
                .expectNext(LocalDate.of(2017, Month.DECEMBER, 31))
                .expectNext(LocalDate.of(2018, Month.JANUARY, 17))
                .verifyComplete();
    }

    @Test
    void fastestMonoValue(final Operations operations) {
        final Mono<String> left = Mono.defer(() -> Mono.delay(Duration.ofSeconds(20L)).map(l -> "foo"));
        final Mono<String> right = Mono.defer(() -> Mono.delay(Duration.ofSeconds(19L)).map(l -> "bar"));
        final Mono<String> mono = operations.fastestMonoValue(left, right);
        StepVerifier.withVirtualTime(() -> mono)
                .thenAwait(Duration.ofSeconds(19L))
                .expectNext("bar")
                .verifyComplete();
                
    }

    @Test
    void firstEmittingNotMixed(final Operations operations) {
        final Flux<String> left = Flux.defer(
                () -> Flux.just("foo", "bar", "baz").delaySequence(Duration.ofSeconds(10L)).delayElements(Duration.ofSeconds(2L)));
        final Flux<String> right = Flux.defer(() -> Flux.interval(Duration.ofSeconds(9L), Duration.ofSeconds(3L)).map(v -> String.format("item-%d", v)).take(3L));
        StepVerifier.withVirtualTime(() -> operations.firstEmittingNotMixed(left, right))
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(9L))
                .expectNext("item-0")
                .expectNoEvent(Duration.ofSeconds(3L))
                .expectNext("item-1")
                .expectNoEvent(Duration.ofSeconds(3L))
                .expectNext("item-2")
                .verifyComplete();
    }
}
