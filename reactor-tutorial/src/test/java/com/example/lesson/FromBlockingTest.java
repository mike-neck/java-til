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
import com.example.lesson.api.FromBlocking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.*;

@ExtendWith({ParameterSupplier.class})
@Lesson(11)
class FromBlockingTest {

    private final ClassLoader loader = getClass().getClassLoader();

    private InputStream inputStream(final String name) {
        return loader.getResourceAsStream(name);
    }

    private Reader reader(final String name) {
        return new InputStreamReader(inputStream(name));
    }

    private BufferedReader bufferedReader(@SuppressWarnings("SameParameterValue") final String name) {
        return new BufferedReader(reader(name));
    }

    @Test
    void fromBlockingWithDeferSubscribeOnScheduler(final FromBlocking fromBlocking) throws IOException {
        try(final BufferedReader reader = bufferedReader("from-blocking-test.txt")) {
            final Flux<String> flux = fromBlocking.fromBlockingToFluxWithScheduler(reader);
            StepVerifier.create(flux)
                    .expectNext("foo")
                    .expectNext("bar")
                    .expectNext("baz")
                    .verifyComplete();
        }
    }
}
