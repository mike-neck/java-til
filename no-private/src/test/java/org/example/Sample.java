/*
 * Copyright 2017 Shinya Mochida
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
package org.example;

import com.example.value.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Sample {

    private List<String> list;

    @BeforeEach
    void setup() {
        list = new ArrayList<>();
    }

    @Nested
    class Foo {
        @BeforeEach
        void setup() {
            list.add("foo");
        }
        @Test
        void test() {
            assertEquals(singletonList("foo"), list);
        }

        @Nested
        class Bar {
            @BeforeEach
            void setup() {
                list.add("bar");
            }
            @Test
            void test() {
                assertEquals(asList("foo", "bar"), list);
            }
 
            @Nested
            class Baz {
                @BeforeEach
                void setup() {
                    list.add("baz");
                }
                @Test
                void test() {
                    assertEquals(asList("foo", "bar", "baz"), list);
                }
            }
        }

        @Nested
        class Qux {
            @BeforeEach
            void setup() {
                list.add("qux");
            }
            @Test
            void test() {
                assertEquals(Arrays.asList("foo", "qux"), list);
            }
        }
    }

    @Test
    void test() {
        assertEquals(emptyList(), list);
    }

    @Test
    void testMockito() {
        final UserId mock = mock(UserId.class);
        when(mock.getValue()).thenReturn(300L);
        when(mock.getValue()).thenReturn(200L);

        assertEquals(200L, mock.getValue());
    }
}
