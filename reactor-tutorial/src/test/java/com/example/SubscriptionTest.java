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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Lesson(6)
@ExtendWith({ ParameterSupplier.class })
class SubscriptionTest {

    @Test
    void backPressure(final SubscribeWithStepVerifier subscribeWithStepVerifier) {
        final List<Executable> executables = new ArrayList<>();
        final Flux<Long> flux = Flux.create(fluxSink -> {
            final long requested = fluxSink.requestedFromDownstream();
            executables.add(() -> assertEquals(4L, requested));
            fluxSink.next(1L)
                    .next(2L)
                    .next(3L)
                    .next(4L)
                    .complete();
        });
        final StepVerifier stepVerifier = subscribeWithStepVerifier.requestAll(flux);
        executables.add(stepVerifier::verify);
        flux.doOnComplete(() -> assertAll(executables.stream()));
    }
}
