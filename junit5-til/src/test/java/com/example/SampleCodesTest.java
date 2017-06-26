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
package com.example;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class SampleCodesTest {

    private static class Pair<K, V> {
        private final K key;
        private final V value;

        private Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        K getKey() {
            return key;
        }

        V getValue() {
            return value;
        }
    }

    private static <K,V> Pair<K, V> kv(K key, V value) {
        return new Pair<>(key, value);
    }

    @SafeVarargs
    private static <K,V> Map<K, V> mapOf(Pair<K, V>... pairs) {
        final Map<K, V> map =  new HashMap<>();
        for (Pair<K, V> pair : pairs) {
            map.put(pair.getKey(), pair.getValue());
        }
        return map;
    } 

    @Test
    void firstTest() {
        final Map<Long, UserEntity> map = SampleCodesTest.mapOf(
                kv(1L, new UserEntity(1L, "ユーザー1", "test1@example.com", "password1")),// 
                kv(3L, new UserEntity(3L, "ユーザー3", "test3@example.com", "password3"))//
        );

        final UserEntity u1 = map.get(1L);
        assumeTrue(u1 != null);
        assertEquals(1L, u1.getId());
        assertEquals("ユーザー1", u1.getName());
        assertEquals("test1@example.com", u1.getEmail());

        final UserEntity u2 = map.get(2L);
        assumeTrue(u2 != null);
        assertEquals(2L, u2.getId());
        assertEquals("ユーザー2", u2.getName());
        assertEquals("test2@example.com", u2.getEmail());

        final UserEntity u3 = map.get(3L);
        assumeTrue(u3 != null);
        assertEquals(3L, u3.getId());
    }
}
