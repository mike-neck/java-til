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
package com.example.lesson.api;

import com.example.annotations.Lesson;
import reactor.core.publisher.Flux;

import java.time.temporal.TemporalUnit;

@Lesson(1)
public interface FluxSupplier {

    Flux<String> emptyFlux();

    Flux<String> fromValues(String... values);

    Flux<String> fromIterable(Iterable<String> iterable);

    Flux<String> error();

    Flux<Long> interval(long duration, TemporalUnit unit);
}