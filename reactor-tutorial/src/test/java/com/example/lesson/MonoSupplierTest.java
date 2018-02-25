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

import com.example.lesson.api.MonoSupplier;
import com.example.ParameterSupplier;
import com.example.annotations.Lesson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.test.StepVerifier;

import java.time.Duration;

@Lesson(2)
@ExtendWith({ ParameterSupplier.class })
class MonoSupplierTest {

    @Test
    void emptyMono(final MonoSupplier monoSupplier) {
        StepVerifier.create(monoSupplier.empty())
                .verifyComplete();
    }

    @Test
    void neverEmit(final MonoSupplier monoSupplier) {
        StepVerifier.create(monoSupplier.never())
                .expectNoEvent(Duration.ofSeconds(1L));
    }

    @Test
    void justMono(final MonoSupplier monoSupplier) {
        StepVerifier.create(monoSupplier.just("foo"))
                .expectNext("foo")
                .verifyComplete();
    }
}
