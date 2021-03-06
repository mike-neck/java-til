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

import com.example.annotations.Lesson;
import com.example.lesson.api.BlockingWriter;
import com.example.lesson.api.FromBlocking;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;

@Lesson(11)
public class FromBlockingImpl implements FromBlocking {

    @Override
    public Flux<String> fromBlockingToFluxWithScheduler(final BufferedReader reader) {
        return Flux.defer(() -> Flux.fromStream(reader.lines())).subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<Void> fluxToBlockingConsumer(final Flux<String> flux, final BlockingWriter writer) {
        return flux.publishOn(Schedulers.single()).doOnNext(writer::write).then();
    }
}
