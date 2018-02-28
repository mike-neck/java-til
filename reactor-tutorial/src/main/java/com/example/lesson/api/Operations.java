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
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Month;

@Lesson(9)
public interface Operations {

    Flux<LocalDate> zipFluxToLocalDate(Flux<Integer> yearFlux, Flux<Month> monthFlux, Flux<Integer> dayOfMonthFlux);

    Mono<String> fastestMonoValue(Mono<String> left, Mono<String> right);

    Flux<String> firstEmittingNotMixed(Flux<String> left, Flux<String> right);
}
