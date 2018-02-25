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
import com.example.lesson.api.ReactiveXAdapter;
import io.reactivex.Flowable;
import reactor.core.publisher.Flux;

@Lesson(8)
public class ReactiveXAdapterImpl implements ReactiveXAdapter {

    @Override
    public Flowable<String> fromFluxToFlowable(final Flux<String> flux) {
        return Flowable.fromPublisher(flux);
    }

    @Override
    public Flux<String> fromFlowableToFlux(final Flowable<String> flowable) {
        return Flux.from(flowable);
    }

}
