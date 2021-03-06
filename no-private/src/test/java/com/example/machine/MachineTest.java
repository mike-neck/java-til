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
package com.example.machine;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MachineTest {

    @Nested
    class Left10Right0 {

        private final Machine machine = Machine.of(10, 0);

        @Test
        void toStringTest() {
            assertEquals("10_0", machine.toString());
        }

        @Nested
        class Add {

            @Test
            void add10() {
                final Machine actual = machine.add(10);

                assertEquals("10_20", actual.toString());
            }

            @Test
            void add0() {
                final Machine actual = machine.add(0);

                assertEquals("10_10", actual.toString());
            }
        }

        @Nested
        class Sub {

            @Test
            void sub10() {
                final Machine actual = machine.sub(10);

                assertEquals("10_0", actual.toString());
            }

            @Test
            void sub0() {
                final Machine actual = machine.sub(0);

                assertEquals("10_10", actual.toString());
            }
        }

        @Nested
        class Set1 {

            private final Machine.Setter machine1Setter = machine.set(1);

            @Test
            void apply20() {
                final Machine actual = machine1Setter.apply(20);

                assertEquals("20_0", actual.toString());
            }

            @Test
            void apply0() {
                final Machine actual = machine1Setter.apply(0);

                assertEquals("0_0", actual.toString());
            }
        }
    }

    @Nested
    class Left0Right10 {

        private final Machine machine = Machine.of(0, 10);

        @Nested
        class Add {

            @Test
            void add10() {
                final Machine actual = machine.add(10);

                assertEquals("0_10", actual.toString());
            }

            @Test
            void add0() {
                final Machine actual = machine.add(0);

                assertEquals("0_0", actual.toString());
            }
        }

        @Nested
        class Sub {

            @Test
            void sub10() {
                final Machine actual = machine.sub(10);

                assertEquals("0_-10", actual.toString());
            }
        }
    }
}
