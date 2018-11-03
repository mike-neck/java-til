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
package com.example.stream;

import io.rsocket.*;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.NettyContextCloseable;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Loggers;

interface Server {}

interface Client {}

public class Streaming {

  private static final Logger serverLogger = LoggerFactory.getLogger(Server.class);
  private static final Logger clientLogger = LoggerFactory.getLogger(Client.class);

  private static Mono<RSocket> startClient() {
    return RSocketFactory.connect()
        .transport(() -> TcpClientTransport.create("localhost", 7000))
        .start();
  }

  private static Function<Payload, String> showDataAndMetadata =
      payload ->
          String.format("(%d) data: %s, metadata: %s", payload.refCnt(), payload.getDataUtf8(),
                  payload.getMetadataUtf8());

  public static void main(String[] args) throws InterruptedException {
    final Disposable server = startServer().subscribe();

    final Flux<Long> clientIdFlux = Flux.interval(Duration.ofMillis(250L)).take(4);
    final Flux<Mono<RSocket>> clientGenerator =
        Flux.fromStream(Stream.generate(Streaming::startClient));

    final CountDownLatch latch = new CountDownLatch(4);

    final Disposable clients =
        Flux.zip(clientIdFlux, clientGenerator)
            .flatMap(
                t -> {
                  final String data = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
                  final String metadata = String.format("client-%d", t.getT1());
                  final Mono<RSocket> socket = t.getT2();
                  return socket.map(
                      rsocket -> rsocket.requestStream(DefaultPayload.create(data, metadata)));
                })
            .flatMap(
                payloadFlux ->
                    payloadFlux.map(showDataAndMetadata).take(10L).doOnComplete(latch::countDown))
            .subscribe(clientLogger::info);

    latch.await();
    clients.dispose();
    server.dispose();
  }

  private static Mono<NettyContextCloseable> startServer() {
    return RSocketFactory.receive()
        .acceptor(serverAcceptor)
        .transport(() -> TcpServerTransport.create("localhost", 7000))
        .start();
  }

  private static final SocketAcceptor serverAcceptor =
      ((setup, sendingSocket) -> {
        serverLogger.info(
            "setup[mime-type:{}, metadata: {}, data: {}]",
            setup.dataMimeType(),
            setup.getMetadataUtf8(),
            setup.getDataUtf8());
        return Mono.fromSupplier(
            () ->
                rsocket()
                    .noReqRes()
                    .reqStream(
                        payload -> {
                          serverLogger.info(
                              "receive({}): [metadata: {}, data: {}]",
                              payload.refCnt(),
                              payload.getDataUtf8(),
                              payload.getMetadataUtf8());
                          return Flux.interval(Duration.ofMillis(200L))
                              .map(
                                  id ->
                                      String.format("data: %s, id: %d", payload.getDataUtf8(), id))
                              .log(Loggers.getLogger(Server.class))
                              .map(
                                  message ->
                                      DefaultPayload.create(message, payload.getMetadataUtf8()));
                        }));
      });

  private static ReqRes rsocket() {
    return requestResponse -> requestStream -> new RSocketImpl(requestResponse, requestStream);
  }

  interface ReqRes {
    default ReqStream noReqRes() {
      return requestResponse(null);
    }

    ReqStream requestResponse(
        final Function<? super Payload, ? extends Mono<Payload>> requestResponse);
  }

  interface ReqStream {
    default RSocket noReqStream() {
      return reqStream(null);
    }

    RSocket reqStream(
        @Nullable final Function<? super Payload, ? extends Flux<Payload>> requestStream);
  }

  static class RSocketImpl extends AbstractRSocket {
    @Nullable private final Function<? super Payload, ? extends Mono<Payload>> requestResponse;
    @Nullable private final Function<? super Payload, ? extends Flux<Payload>> requestStream;

    RSocketImpl(
        @Nullable Function<? super Payload, ? extends Mono<Payload>> requestResponse,
        @Nullable Function<? super Payload, ? extends Flux<Payload>> requestStream) {
      this.requestResponse = requestResponse;
      this.requestStream = requestStream;
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
      if (requestStream == null) {
        return super.requestStream(payload);
      }
      return requestStream.apply(payload);
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
      if (requestResponse == null) {
        return super.requestResponse(payload);
      }
      return requestResponse.apply(payload);
    }
  }
}
