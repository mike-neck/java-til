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
package com.example.channel;

import io.rsocket.*;
import io.rsocket.transport.netty.server.NettyContextCloseable;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Server extends AbstractRSocket {

  private static final Logger logger = LoggerFactory.getLogger(Server.class);

  private static <T> Subscriber<T> subscriber(final Consumer<? super T> consumer) {
    return new Subscriber<T>() {
      @Override
      public void onSubscribe(Subscription s) {
        s.request(1L);
      }

      @Override
      public void onNext(T t) {
        consumer.accept(t);
      }

      @Override
      public void onError(Throwable t) {
        logger.warn("subscriber", t);
      }

      @Override
      public void onComplete() {}
    };
  }

  @Override
  public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
    return Flux.from(payloads)
        .doOnEach(
            signal ->
                signal.accept(
                    subscriber(
                        payload ->
                            logger.info(
                                "channel: [data: {}, metadata: {}]",
                                payload.getDataUtf8(),
                                payload.getMetadataUtf8()))))
        .map(
            payload ->
                DefaultPayload.create(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    payload.getMetadataUtf8()));
  }

  static Mono<NettyContextCloseable> start() {
    return RSocketFactory.receive()
        .acceptor((setup, sendingSocket) -> Mono.just(new Server()))
        .transport(() -> TcpServerTransport.create("localhost", 7000))
        .start();
  }
}
