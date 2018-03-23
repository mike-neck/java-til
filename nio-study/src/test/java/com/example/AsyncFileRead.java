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

import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.EmitterProcessor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
class AsyncFileRead {

    @Test
    void read() throws IOException, InterruptedException {
        final ExecutorService mainLoop = Executors.newSingleThreadExecutor();
        final ExecutorService executor = Executors.newFixedThreadPool(1);
        final ExecutorService subLoop = Executors.newFixedThreadPool(1);

        final Path filePath = Paths.get("sample", "file.txt");
        if (!Files.exists(filePath)) {
            log.info("file not found: {}", filePath);
            return;
        }

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final long size = Files.size(filePath);
        final int bufferSize = 4;

        final EmitterProcessor<ByteBuffer> processor = EmitterProcessor.create();
        final ConnectableFlux<ByteBuffer> flux = processor.delayElements(Duration.ofMillis(50L)).log().publish();

        mainLoop.submit(() -> {
            log.info("read file: {}, size: {}", filePath, size);
            try (final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(filePath, Set.of(StandardOpenOption.READ), executor)) {
                final CountDownLatch latch = new CountDownLatch(1);
                for (long position = 0; position < size; position += bufferSize) {
                    final long pos = position;
                    final ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
                    final Future<Integer> future = fileChannel.read(byteBuffer, pos);
                    CompletableFuture.runAsync(() -> {
                        try {
                            final Integer read = future.get();
                            log.info("load: size: {}, position: {}", read, pos);
                            processor.onNext(byteBuffer.flip());
                            if (size <= pos + bufferSize) {
                                log.info("done: total: {}", pos + read);
                                processor.onComplete();
                                latch.countDown();
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            processor.onError(e);
                            latch.countDown();
                        }
                    }, subLoop);
                }
                latch.await();
            } catch (IOException | InterruptedException e) {
                log.warn("error", e);
            }
            log.info("file read");
        });

        flux.map(ByteBuffer::array)
                .map(ByteLists.immutable::of)
                .reduceWith(ByteLists.mutable::empty, MutableByteList::withAll)
                .map(ByteIterable::toArray)
                .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                .subscribe(text -> log.info("result:\n{}", text));
        flux.doOnComplete(() -> {
            log.info("complete");
            countDownLatch.countDown();
        }).doOnError(e -> {
            log.info("error");
            countDownLatch.countDown();
        });

        flux.connect();

        countDownLatch.await();
        mainLoop.shutdown();
        executor.shutdown();
        subLoop.shutdown();
        log.info("application finish");
    }
}
