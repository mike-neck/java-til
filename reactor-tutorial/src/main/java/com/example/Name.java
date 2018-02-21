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

import org.eclipse.collections.api.block.function.primitive.CharIntToObjectFunction;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.impl.factory.primitive.CharLists;

public interface Name {

    String asString();

    default Name capitalize() {
        final String value = asString();
        final String capitalized = value.codePoints()
                .collect(CharLists.mutable::empty, (list, value1) -> list.add((char) value1), MutableCharList::addAll)
                .collectWithIndex((CharIntToObjectFunction<Character>) (ch, index) -> index == 0 ? Character.toUpperCase(ch) : ch)
                .makeString();
        return () -> capitalized;
    }
}
