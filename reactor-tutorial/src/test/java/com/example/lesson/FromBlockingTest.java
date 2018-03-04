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
import com.example.lesson.api.BlockingWriter;
import com.example.lesson.api.FromBlocking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.*;
import java.time.Duration;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.*;

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
        try (final BufferedReader reader = bufferedReader("from-blocking-test.txt")) {
            final Flux<String> flux = fromBlocking.fromBlockingToFluxWithScheduler(reader);
            StepVerifier.create(flux)
                    .expectNext("foo")
                    .expectNext("bar")
                    .expectNext("baz")
                    .verifyComplete();
        }
    }

    @Test
    void toBlockingConsumerWithPublishOnScheduler(final FromBlocking fromBlocking) {
        final BlockingWriterImpl writer = new BlockingWriterImpl();
        final Flux<String> tester = Flux.create(writer::setSink).log()
                .subscribeOn(Schedulers.parallel());

        final Flux<String> flux = Flux.just("foo", "bar", "baz").log()
                .doOnTerminate(writer::close);

        final Mono<Void> mono = fromBlocking.fluxToBlockingConsumer(flux, writer);
        final ExecutorService executors = Executors.newFixedThreadPool(2);
        final CompletableFuture<Duration> future1 = CompletableFuture.supplyAsync(
                () -> StepVerifier.create(mono).verifyComplete(), executors);
        final CompletableFuture<Duration> future2 = CompletableFuture.supplyAsync(
                () -> StepVerifier.create(tester)
                        .expectNext("foo")
                        .expectNext("bar")
                        .expectNext("baz")
                        .verifyComplete()
        , executors);
        final CompletableFuture<Void> finish = CompletableFuture.allOf(future1, future2);
        finish.join();
        executors.shutdown();
    }
}

class BlockingWriterImpl implements BlockingWriter {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Queue<String> queue = new ConcurrentLinkedQueue<>();

    private FluxSink<String> sink;

    public void setSink(final FluxSink<String> sink) {
        this.sink = sink;
        sink.onRequest(this::publish);
        sink.onDispose(() -> {
            System.out.println("disposed.");
            executor.shutdown();
        });
        sink.onCancel(() -> System.out.println("cancel - tester"));
    }

    private void publish(final long request) {
        final long size = request <= queue.size() ? request : queue.size();
        System.out.println(String.format("request: %d", request));
        for (long i = 0; i < size; i++) {
            final String poll = queue.poll();
            System.out.println(String.format("next: %s", poll));
            sink.next(poll);
        }
    }

    @Override
    public void write(final String value) {
        System.out.println(value);
        queue.offer(value);
    }

    @Override
    public void close() {
        System.out.println("closing");
        CompletableFuture.runAsync(() -> Objects.requireNonNull(sink).complete(), executor);
    }
}
