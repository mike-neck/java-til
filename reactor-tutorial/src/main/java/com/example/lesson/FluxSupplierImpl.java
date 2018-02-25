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

import com.example.annotations.Lesson;
import com.example.lesson.api.FluxSupplier;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

@Lesson(1)
public class FluxSupplierImpl implements FluxSupplier {

    @Override
    public Flux<String> emptyFlux() {
        return Flux.empty();
    }

    @Override
    public Flux<String> fromValues(final String... values) {
        return Flux.just(values);
    }

    @Override
    public Flux<String> fromIterable(final Iterable<String> iterable) {
        return Flux.fromIterable(iterable);
    }

    @Override
    public Flux<String> error() {
        return Flux.error(new IllegalStateException(), true);
    }

    @Override
    public Flux<Long> interval(final long duration, final TemporalUnit unit) {
        return Flux.interval(Duration.of(duration, unit)).take(10);
    }
}
