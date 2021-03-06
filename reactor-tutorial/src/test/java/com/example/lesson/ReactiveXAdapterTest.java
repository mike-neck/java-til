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
import com.example.lesson.api.ReactiveXAdapter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Lesson(8)
@ExtendWith({ParameterSupplier.class})
class ReactiveXAdapterTest {

    @Test
    void fromFluxToFlowable(final ReactiveXAdapter adapter) {
        final Flux<String> flux = Flux.just("foo", "bar", "baz");
        final Flowable<String> flowable = adapter.fromFluxToFlowable(flux);
        StepVerifier.create(flowable)
                .expectSubscription()
                .expectNext("foo")
                .expectNext("bar")
                .expectNext("baz")
                .verifyComplete();
    }

    @Test
    void fromFlowableToFlux(final ReactiveXAdapter adapter) {
        final Flowable<String> flowable = Flowable.fromArray("foo", "bar", "baz", "qux");
        final Flux<String> flux = adapter.fromFlowableToFlux(flowable);
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("foo")
                .expectNext("bar", "baz", "qux")
                .verifyComplete();
    }

    @Test
    void fromFluxToObservable(final ReactiveXAdapter adapter) {
        final Flux<String> flux = Flux.just("foo", "bar", "baz");
        final Observable<String> observable = adapter.fromFluxToObservable(flux);
        StepVerifier.create(observable.toFlowable(BackpressureStrategy.BUFFER))
                .expectSubscription()
                .expectNext("foo")
                .expectNext("bar")
                .expectNext("baz")
                .verifyComplete();
    }

    @Test
    void fromObservableToFlux(final ReactiveXAdapter adapter) {
        final Observable<String> observable = Observable.fromArray("foo", "bar", "baz", "qux");
        final Flux<String> flux = adapter.fromObservableToFlux(observable);
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("foo", "bar")
                .expectNext("baz", "qux")
                .verifyComplete();
    }

    @Test
    void fromMonoToSingle(final ReactiveXAdapter adapter) {
        final Mono<String> mono = Mono.just("foo");
        final Single<String> single = adapter.fromMonoToSingle(mono);
        StepVerifier.create(single.toFlowable())
                .expectSubscription()
                .expectNext("foo")
                .verifyComplete();
    }

    @Test
    void fromSingleToMono(final ReactiveXAdapter adapter) {
        final Single<String> single = Single.just("foo");
        final Mono<String> mono = adapter.fromSingleToMono(single);
        StepVerifier.create(mono)
                .expectSubscription()
                .expectNext("foo")
                .verifyComplete();
    }

    @Test
    void fromMonoToCompletableFuture(final ReactiveXAdapter adapter) throws InterruptedException {
        final Mono<String> mono = Mono.just("foo");
        final CompletableFuture<String> future = adapter.fromMonoToCompletableFuture(mono);
        final CountDownLatch latch = new CountDownLatch(1);
        future.thenAccept(string -> assertThat(string).isEqualTo("foo"))
                .thenAccept(v -> latch.countDown());
        latch.await();
    }

    @Test
    void fromFutureToMono(final ReactiveXAdapter adapter) {
        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "foo", executorService);
        final Mono<String> mono = adapter.fromCompletableFutureToMono(future);
        StepVerifier.create(mono.doOnTerminate(executorService::shutdown))
                .expectSubscription()
                .expectNext("foo")
                .verifyComplete();
    }
}
