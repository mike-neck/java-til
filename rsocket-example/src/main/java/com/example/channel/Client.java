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

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import java.net.InetSocketAddress;
import java.time.Duration;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class Client {

  private static final Logger logger = LoggerFactory.getLogger(Client.class);

  private final long clientInterval;
  private final int clientSize;
  private final long messageInterval;
  private final int messageCount;

  Client(long clientInterval, int clientSize, long messageInterval, int messageCount) {
    this.clientInterval = clientInterval;
    this.clientSize = clientSize;
    this.messageInterval = messageInterval;
    this.messageCount = messageCount;
  }

  Flux<Long> createClientStream() {
    return Flux.interval(Duration.ofMillis(clientInterval)).take(clientSize);
  }

  static Mono<RSocket> newClient(final InetSocketAddress serverAddress) {
    return RSocketFactory.connect()
        .transport(() -> TcpClientTransport.create(serverAddress))
        .start();
  }

  Publisher<Payload> request(final long clientId) {
    return Flux.interval(Duration.ofMillis(messageInterval))
        .take(messageCount)
        .map(messageId -> createMessage(clientId, messageId));
  }

  void showPayload(final Payload payload) {
    final String logMessage =
        String.format(
            "receive from server| data: %s, metadata: %s",
            payload.getDataUtf8(), payload.getMetadataUtf8());
    logger.info(logMessage);
  }

  private static Payload createMessage(final long clientId, final long messageId) {
    final String data = String.format("Client(%d)-Message(%d)", clientId, messageId);
    final String metadata = String.format("metadata[%d-%d]", clientId, messageId);
    return DefaultPayload.create(data, metadata);
  }
}
