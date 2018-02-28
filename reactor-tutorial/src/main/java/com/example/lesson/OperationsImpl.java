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
import com.example.lesson.api.Operations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Month;

@Lesson(9)
public class OperationsImpl implements Operations {

    @Override
    public Flux<LocalDate> zipFluxToLocalDate(final Flux<Integer> yearFlux, final Flux<Month> monthFlux, final Flux<Integer> dayOfMonthFlux) {
        return Flux.zip(yearFlux, monthFlux, dayOfMonthFlux).map(t -> LocalDate.of(t.getT1(), t.getT2(), t.getT3()));
    }

    @Override
    public Mono<String> fastestMonoValue(final Mono<String> left, final Mono<String> right) {
        return Mono.first(left, right);
    }

    @Override
    public Flux<String> firstEmittingNotMixed(final Flux<String> left, final Flux<String> right) {
        return Flux.first(left, right);
    }

    @Override
    public Mono<Void> completeWithThen(final Flux<Long> flux) {
        return flux.then();
    }

    @Override
    public Mono<String> nullAwareMono(final String foo) {
        return null;
    }
}
