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

import org.eclipse.collections.impl.factory.Iterables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.test.StepVerifier;

@ExtendWith({ ParameterSupplier.class })
class FluxSupplierTest {

    @Test
    void emptyFlux(final FluxSupplier fluxSupplier) {
        StepVerifier.create(fluxSupplier.emptyFlux())
                .expectComplete()
                .verify();
    }

    @Test
    void fromValues(final FluxSupplier fluxSupplier) {
        StepVerifier.create(fluxSupplier.fromValues("foo", "bar", "baz"))
                .expectNext("foo")
                .expectNext("bar")
                .expectNext("baz")
                .verifyComplete();
    }

    @Test
    void fromIterable(final FluxSupplier fluxSupplier) {
        StepVerifier.create(fluxSupplier.fromIterable(Iterables.iList("foo", "bar", "baz")))
                .expectNext("foo", "bar", "baz")
                .verifyComplete();
    }
}
