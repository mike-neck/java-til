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
import com.example.lesson.api.ToBlocking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({ParameterSupplier.class})
@Lesson(10)
class ToToBlockingTest {

    @Test
    void blockMono(final ToBlocking toBlocking) {
        final String string = toBlocking.blockMono(Mono.just("foo"));
        assertThat(string).isEqualTo("foo");
    }

    @Test
    void fluxToIterable(final ToBlocking toBlocking) {
        final Iterable<String> iterable = toBlocking.fluxToIterable(Flux.just("foo", "bar", "baz"));
        assertThat(iterable).containsExactly("foo", "bar", "baz");
    }

    @Test
    void fluxFirstElement(final ToBlocking toBlocking) {
        final Flux<String> flux = Flux.just("foo", "bar", "baz");
        final String firstElement = toBlocking.firstElement(flux);
        assertThat(firstElement).isEqualTo("foo");
    }
}
