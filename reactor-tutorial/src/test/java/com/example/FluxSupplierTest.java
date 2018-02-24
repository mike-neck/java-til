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
import org.eclipse.collections.impl.factory.Iterables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Lesson(1)
@ExtendWith({ ParameterSupplier.class })
class FluxSupplierTest {

    @Test
    void emptyFlux(final FluxSupplier fluxSupplier) {
        StepVerifier.create(fluxSupplier.emptyFlux())
                .expectComplete()
                .verify();
    }

    @Test
    void fromValues(final FluxSupplier fluxSupplier) {
        StepVerifier.create(fluxSupplier.fromValues("foo", "bar", "baz"))
                .expectNext("foo")
                .expectNext("bar")
                .expectNext("baz")
                .verifyComplete();
    }

    @Test
    void fromIterable(final FluxSupplier fluxSupplier) {
        StepVerifier.create(fluxSupplier.fromIterable(Iterables.iList("foo", "bar", "baz")))
                .expectNext("foo", "bar", "baz")
                .verifyComplete();
    }

    @Test
    void error(final FluxSupplier fluxSupplier) {
        StepVerifier.create(fluxSupplier.error())
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void interval(final FluxSupplier fluxSupplier) {
        StepVerifier.create(fluxSupplier.interval(10L, ChronoUnit.MILLIS))
                .expectNext(0L)
                .thenAwait(Duration.ofMillis(10L))
                .expectNext(1L)
                .thenAwait(Duration.ofMillis(10L))
                .expectNextCount(8L)
                .verifyComplete();
        StepVerifier.create(fluxSupplier.interval(100L, ChronoUnit.MILLIS))
                .expectNextCount(10)
                .verifyComplete();
    }
}
