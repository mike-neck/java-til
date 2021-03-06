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
import com.example.lesson.api.ErrorHandle;
import com.example.lesson.api.FooBarSizeException;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Lesson(7)
public class ErrorHandleImpl implements ErrorHandle {

    @Override
    public Mono<String> onErrorReturnFoo(final Mono<String> mono) {
        return mono.onErrorReturn("foo");
    }

    @Override
    public Flux<String> onErrorResumeFluxBarBaz(final Flux<String> flux) {
        return flux.onErrorResume(any -> Flux.just("bar", "baz"));
    }

    @Override
    public Flux<String> propagateException(final Flux<String> flux) {
        return flux.map(string -> {
            try {
                return ErrorHandle.capitalizeFooBar(string);
            } catch (FooBarSizeException e) {
                throw  Exceptions.propagate(e);
            }
        });
    }
}
