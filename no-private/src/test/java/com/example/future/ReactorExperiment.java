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
package com.example.future;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

class ReactorExperiment {

    private static final ExecutorService executor = Executors.newFixedThreadPool(3);

    private static void sleep() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void shutdown() {
        executor.shutdown();
    }

    enum Result implements Function<String, String> {
        SUCCESS {
            @Override
            public String apply(String s) {
                return String.format("%s - %s", s, s);
            }
        },
        FAIL {
            @Override
            public String apply(String s) {
                throw new RuntimeException(String.format("failure: %s", s));
            }
        }
    }

    @Disabled
    @DisplayName("Reactorの実験 - これはダメな例")
    @Test
    void reactorExperiment() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Result result = Result.FAIL;
        final CompletableFuture<String> future = firstFuture();
        final Mono<String> futureMono = Mono.fromFuture(future).log("1st");
        final Mono<String> sinkMono = futureMono.flatMap(string -> Mono.<String>create(sink -> {
            final CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
                sleep();
                System.out.println("mapping");
                return result.apply(string);
            }, executor);
            f.whenComplete((s, t) -> {
                if (t == null && s == null) {
                    sink.error(new RuntimeException("both are null"));
                } else if (t == null) {
                    sink.success(s);
                } else {
                    sink.error(t);
                }
            });
        }).log("2nd")).log("3rd");
        sinkMono.subscribe(System.out::println);
        sinkMono.doOnError(Throwable::printStackTrace);
        sinkMono.doOnTerminate(latch::countDown);
        sinkMono.doOnCancel(latch::countDown);
        latch.await();
    }

    private CompletableFuture<String> firstFuture() {
        return CompletableFuture.supplyAsync(() -> {
                sleep();
                System.out.println("start");
                return "FOO";
            }, executor);
    }

    @Test
    void reactorExperiment2() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Result result = Result.FAIL;

        final CompletableFuture<String> firstFuture = firstFuture();
        final Mono<String> firstMono = Mono.fromFuture(firstFuture);

        final CompletableFuture<String> jointFuture = new CompletableFuture<>();
        firstMono.doOnError(jointFuture::completeExceptionally).subscribe(jointFuture::complete);

        final CompletableFuture<String> nextFuture = jointFuture.thenComposeAsync(str -> {
            sleep();
            System.out.println("mapping");
            return CompletableFuture.supplyAsync(() -> result.apply(str));
        }, executor);

        Mono.fromFuture(nextFuture)
                .doOnError(Throwable::printStackTrace)
                .doOnTerminate(latch::countDown)
                .doOnCancel(latch::countDown)
                .subscribe(System.out::println);
        latch.await();
    }
}
