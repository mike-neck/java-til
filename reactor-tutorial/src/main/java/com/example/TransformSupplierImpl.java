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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TransformSupplierImpl implements TransformSupplier {

    @Override
    public Mono<Name> mappingMono(final Mono<User> mono) {
        return mono.map(User::capitalizedName);
    }

    @Override
    public Flux<Name> mappingFlux(final Flux<User> flux) {
        return flux.map(User::capitalizedName);
    }

    @Override
    public Flux<Name> flatMappingFlux(final Flux<User> flux) {
        return flux.flatMap(User::asyncCapitalizedName);
    }
}
