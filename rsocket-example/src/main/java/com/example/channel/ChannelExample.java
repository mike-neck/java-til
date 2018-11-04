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

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public class ChannelExample {

  public static void main(String[] args) throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final Disposable serverDisposable = Server.start().subscribe();

    final Disposable clientDisposable =
        Flux.interval(Duration.ofMillis(125L))
            .take(6L)
            .map(clientId -> new Client(clientId, 300L, 20))
            .flatMap(Client::runApplication)
            .doOnTerminate(latch::countDown)
            .subscribe(Client::showPayload);

    latch.await();

    clientDisposable.dispose();
    serverDisposable.dispose();
  }
}
