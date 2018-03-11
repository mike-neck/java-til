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
package com.example.share;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

import java.util.concurrent.*;

@Slf4j
public final class Futures {

    private static final Futures instance = new Futures();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Futures() {
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
    }

    private <S> CompletableFuture<S> completableFuture(final Future<? extends S> future) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.warn("exception in executing task.", e);
                throw new RuntimeException(e);
            }
        }, executor);
    }

    public static <S> CompletableFuture<S> toCompletableFuture(final Future<? extends S> future) {
        return instance.completableFuture(future);
    }

    private <S> Mono<S> mono(final Future<? extends S> future) {
        final MonoProcessor<S> processor = MonoProcessor.create();
        executor.submit(() -> {
            try {
                final S stat = future.get();
                processor.onNext(stat);
                processor.onComplete();
            } catch (InterruptedException | ExecutionException e) {
                processor.onError(e);
            }
        });
        return processor;
    } 

    public static <S> Mono<S> toMono(final Future<? extends S> future) {
        return instance.mono(future);
    }
}
