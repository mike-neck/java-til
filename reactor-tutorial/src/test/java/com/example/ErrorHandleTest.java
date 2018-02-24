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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Lesson(7)
@ExtendWith({ ParameterSupplier.class })
class ErrorHandleTest {

    @Test
    void onErrorReturn(final ErrorHandle errorHandle) {
        final Mono<String> mono = Mono.fromCallable(() -> {
            throw new IllegalStateException("expected to be thrown.");
        });
        StepVerifier.create(errorHandle.onErrorReturnFoo(mono))
                .expectSubscription()
                .expectNext("foo")
                .verifyComplete();
    }
}
