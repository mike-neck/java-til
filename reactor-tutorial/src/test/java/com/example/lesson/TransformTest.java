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
import com.example.lesson.api.TransformSupplier;
import com.example.User;
import com.example.annotations.Lesson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Lesson(4)
@ExtendWith({ ParameterSupplier.class })
class TransformTest {

    @Test
    void mappingMono(final TransformSupplier transformSupplier) {
        final Mono<User> mono = Mono.just(() -> () -> "miguel");
        StepVerifier.create(transformSupplier.mappingMono(mono))
                .assertNext(name -> assertThat(name.asString()).isEqualTo("Miguel"))
                .verifyComplete();
    }

    @Test
    void mappingFlux(final TransformSupplier transformSupplier) {
        final Flux<User> flux = createFlux();
        StepVerifier.create(transformSupplier.mappingFlux(flux))
                .assertNext(name -> assertThat(name.asString()).isEqualTo("User"))
                .assertNext(name -> assertThat(name.asString()).isEqualTo("1st"))
                .assertNext(name -> assertThat(name.asString()).isEqualTo("Impossible"))
                .verifyComplete();
    }

    private static Flux<User> createFlux() {
        final Stream<User> stream = Stream.of("user", "1st", "impossible").map(name -> () -> () -> name);
        return Flux.fromStream(stream);
    }

    @Test
    void flatMappingFlux(final TransformSupplier transformSupplier) {
        final Flux<User> flux = createFlux();
        StepVerifier.withVirtualTime(() -> transformSupplier.flatMappingFlux(flux))
                .thenAwait(Duration.ofSeconds(User.DELAY * 4))
                .assertNext(name -> assertThat(name.asString()).isEqualTo("User"))
                .assertNext(name -> assertThat(name.asString()).isEqualTo("1st"))
                .assertNext(name -> assertThat(name.asString()).isEqualTo("Impossible"))
                .verifyComplete();
    }
}
