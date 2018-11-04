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
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ChannelExample {

  public static void main(String[] args) throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final Disposable serverDisposable = Server.start().subscribe();

    final Client client = new Client(250L, 4, 400L, 10);
    final Mono<RSocket> mono = Client.newClient(new InetSocketAddress("localhost", 7000));

    final Disposable clientDisposable =
        mono.flux()
            .flatMap(
                rsocket -> {
                  final Flux<Long> clients = client.createClientStream();
                  final Flux<Payload> payloads = clients.flatMap(client::request);
                  return rsocket.requestChannel(payloads);
                })
            .doOnTerminate(latch::countDown)
            .subscribe(client::showPayload);

    latch.await();

    clientDisposable.dispose();
    serverDisposable.dispose();
  }
}
