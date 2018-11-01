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

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

enum Client {}

enum Server {}

public class HelloWorld {
  private static final Logger logger = LoggerFactory.getLogger(HelloWorld.class);
  private static final Logger serverLogger = LoggerFactory.getLogger(Server.class);
  private static final Logger clientLogger = LoggerFactory.getLogger(Client.class);

  public static void main(String[] args) throws Exception {
    final Disposable serverDisposable =
        RSocketFactory.receive()
            .acceptor(
                (setup, sendingSocket) -> {
                  serverLogger.info(
                      "setup: {}, data: {}", setup.dataMimeType(), setup.getDataUtf8());
                  final AtomicInteger atomicInteger = new AtomicInteger(0);
                  return Mono.just(
                      new AbstractRSocket() {
                        @Override
                        public Mono<Payload> requestResponse(Payload payload) {
                          serverLogger.info(
                              "request payload: {} {}",
                              payload.getMetadataUtf8(),
                              payload.getDataUtf8());
                          final int current = atomicInteger.getAndIncrement();
                          final Payload response =
                              DefaultPayload.create(
                                  String.format(
                                      "response(%d)[%s]",
                                      current, DateTimeFormatter.ISO_INSTANT.format(Instant.now())),
                                  payload.getDataUtf8());
                          return Mono.just(response);
                          //                            if (current % 2 == 0) {
                          //                                final Payload response =
                          // DefaultPayload.create(String.format("response: %d, time: %s", current,
                          //
                          // DateTimeFormatter.ISO_INSTANT.format(Instant.now())));
                          //                                return Mono.just(response);
                          //                            } else {
                          //                                return Mono.error(new
                          // Throwable(String.format("request: %d", current)));
                          //                            }
                        }
                      });
                })
            .transport(() -> TcpServerTransport.create(7000))
            .start()
            .subscribe();

    final CountDownLatch latch = new CountDownLatch(1);

    final long start = System.currentTimeMillis();
    logger.info("start at {}", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));

    final Disposable clientDisposable =
        RSocketFactory.connect()
            .transport(TcpClientTransport.create("localhost", 7000))
            .start()
            .flux()
            .flatMap(
                rsocket ->
                    createPayloads()
                        .flatMap(rsocket::requestResponse)
                        .map(
                            payload ->
                                String.format(
                                    "metadata: %s, body: %s",
                                    payload.getMetadataUtf8(), payload.getDataUtf8())))
            .doOnTerminate(latch::countDown)
            .subscribe(clientLogger::info);

    latch.await();

    logger.info(
        "finish in {} ms at {}",
        System.currentTimeMillis() - start,
        DateTimeFormatter.ISO_INSTANT.format(Instant.now()));

    clientDisposable.dispose();
    serverDisposable.dispose();
  }

  private static Flux<Payload> createPayloads() {
    return Flux.interval(Duration.ofMillis(125L))
        .take(32)
        .flatMap(
            id ->
                Flux.interval(Duration.ofMillis(2L))
                    .take(2000)
                    .map(sub -> String.format("%d - %d", id, sub)))
        .map(message -> DefaultPayload.create(message, StandardCharsets.UTF_8));
  }
}
