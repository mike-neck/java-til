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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ExtendWith({ ParameterSupplier.class })
class StepVerifierRunnerTest {

    @Test
    void fooBarTesting(final StepVerifierRunner stepVerifierRunner) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        stepVerifierRunner.verifyFooBar(Flux.just("foo", "bar").doOnComplete(countDownLatch::countDown));
        countDownLatch.await(200L, TimeUnit.MILLISECONDS);
    }
}
