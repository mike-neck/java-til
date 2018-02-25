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
package com.example.lesson.api;

import com.example.annotations.Lesson;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Lesson(8)
public interface ReactiveXAdapter {

    Flowable<String> fromFluxToFlowable(Flux<String> flux);

    Flux<String> fromFlowableToFlux(Flowable<String> flowable);

    Observable<String> fromFluxToObservable(Flux<String> flux);

    Flux<String> fromObservableToFlux(Observable<String> observable);

    Single<String> fromMonoToSingle(Mono<String> mono);
}
