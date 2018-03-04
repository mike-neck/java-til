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

import com.example.lesson.*;
import com.example.lesson.api.*;
import com.google.inject.AbstractModule;

public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(FluxSupplier.class).to(FluxSupplierImpl.class);
        bind(MonoSupplier.class).to(MonoSupplierImpl.class);
        bind(StepVerifierRunner.class).to(StepVerifierRunnerImpl.class);
        bind(TransformSupplier.class).to(TransformSupplierImpl.class);
        bind(Merger.class).to(MergerImpl.class);
        bind(UserRepository.class).to(UserRepositoryImpl.class);
        bind(SubscribeWithStepVerifier.class).to(SubscribeWithStepVerifierImpl.class);
        bind(ErrorHandle.class).to(ErrorHandleImpl.class);
        bind(ReactiveXAdapter.class).to(ReactiveXAdapterImpl.class);
        bind(Operations.class).to(OperationsImpl.class);
        bind(Blocking.class).to(BlockingImpl.class);
    }
}
