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

import reactor.core.publisher.Mono;

public interface Either<L extends Exception, R> {

    R getOrThrow() throws L;

    default Mono<R> toMono() {
        try {
            final R value = getOrThrow();
            return Mono.just(value);
        } catch (Exception l) {
            return Mono.error(l);
        }
    }

    @SuppressWarnings("unchecked")
    static <L extends Exception, R> Either<L, R> either(final ExSupplier<? extends L, ? extends R> supplier) {
        try {
            final R r;
            r = supplier.get();
            return new Right<>(r);
        } catch (Exception l) {
            return new Left<>((L) l);
        }
    }

    interface ExSupplier<L extends Exception, R>  {
        R get() throws L;
    }
}

class Right<L extends Exception, R> implements Either<L, R> {

    private final R value;

    Right(final R value) {
        this.value = value;
    }

    @Override
    public R getOrThrow() throws L {
        return value;
    }
}

class Left<L extends Exception, R> implements Either<L, R> {

    private final L exception;

    Left(final L exception) {
        this.exception = exception;
    }


    @Override
    public R getOrThrow() throws L {
        throw exception;
    }
}
