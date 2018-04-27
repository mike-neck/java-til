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

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
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
        };
        private static final Random random = new Random();

        static Result random() {
            final int index = random.nextInt(2);
            return Arrays.stream(values())
                    .filter(it -> it.ordinal() == index)
                    .findAny()
                    .orElseThrow(IllegalStateException::new);
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

    static <R> OnSuccess<R> handle(final R result, final Throwable error) {
        if (error == null && result != null) {
            return successConsumer -> errorConsumer -> successConsumer.accept(result);
        } else if (error != null) {
            return successConsumer -> errorConsumer -> errorConsumer.accept(error);
        } else {
            final IllegalStateException exception =
                    new IllegalStateException("not success but not error");
            return successConsumer -> errorConsumer -> errorConsumer.accept(exception);
        }
    }

    interface OnSuccess<R> {
        OnError onSuccess(Consumer<? super R> successConsumer);
    }

    interface OnError {
        void onError(final Consumer<? super Throwable> errorConsumer);
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

    @Test
    void handlers1() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep();
            return Result.random().apply("foo");
        }, executor);

        final CompletableFuture<String> hf1 = future.whenCompleteAsync((str, th) -> handle(str, th)
                .onSuccess(s -> System.out.println(String.format("handler 1 -> %s", s)))
                .onError(e -> System.out.println(String.format("handler 1 error -> %s", e.getClass().getSimpleName())
                )), executor);
        final CompletableFuture<String> hf2 = future.whenCompleteAsync((str, th) -> handle(str, th)
                .onSuccess(s -> System.out.println(String.format("handler 2 -> %s", s)))
                .onError(e -> System.out.println(String.format("handler 2 error -> %s", e.getClass().getSimpleName())
                )), executor);

        CompletableFuture.allOf(hf1, hf2).whenComplete((s, t) -> latch.countDown());
        latch.await();
    }

    @Test
    void handlers2() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch finalLatch = new CountDownLatch(1);
        final CompletableFuture<String> future =
                CompletableFuture.supplyAsync(
                        () -> {
                            sleep();
                            return Result.random().apply("foo");
                        },
                        executor);

        final CompletableFuture<String> f1 =
                future.thenApplyAsync(str -> String.format("result -> %s", str));
        final CompletableFuture<String> f2 =
                future.thenApplyAsync(
                        str -> {
                            throw new RuntimeException(str);
                        });

        final CompletableFuture<String> hf1 =
                f1.whenCompleteAsync(
                        (str, th) ->
                                handle(str, th)
                                        .onSuccess(s -> System.out.println(String.format("handler 1 -> %s", s)))
                                        .onError(
                                                e ->
                                                        System.out.println(
                                                                String.format(
                                                                        "handler 1 error -> %s", e.getClass().getSimpleName()))),
                        executor);
        final CompletableFuture<String> hf2 =
                f2.whenCompleteAsync(
                        (str, th) ->
                                handle(str, th)
                                        .onSuccess(s -> System.out.println(String.format("handler 2 -> %s", s)))
                                        .onError(
                                                e ->
                                                        System.out.println(
                                                                String.format(
                                                                        "handler 2 error -> %s", e.getClass().getSimpleName()))),
                        executor);

        CompletableFuture.allOf(hf1, hf2).whenComplete((s, t) -> latch.countDown());
        latch.await();

        final CompletableFuture<String> f3 =
                future.thenApplyAsync(str -> String.format("after completed: %s", str));
        final CompletableFuture<String> hf3 =
                f3.whenCompleteAsync(
                        (str, th) ->
                                handle(str, th)
                                        .onSuccess(s -> System.out.println(String.format("handler 3 -> %s", s)))
                                        .onError(
                                                e ->
                                                        System.out.println(
                                                                String.format(
                                                                        "handler 3 error -> %s", e.getClass().getSimpleName()))),
                        executor);

        hf3.whenCompleteAsync((s, t) -> finalLatch.countDown());
        finalLatch.await();
    }
}
