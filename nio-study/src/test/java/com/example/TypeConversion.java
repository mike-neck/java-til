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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

@Slf4j
class TypeConversion {

    @Test
    void longToByteArray() {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putLong(0x1122_1133_11ee_11ffL);
        final byte[] bytes = byteBuffer.array();
        final StringBuilder sb = new StringBuilder();
        for (final byte b : bytes) {
            sb.append(String.format("%02x_", b));
        }
        log.info("bytes: {}", sb);
        final long decoded = ByteBuffer.wrap(bytes).getLong();
        log.info("decoded: {}", decoded);
    }

    @Test
    void byteArrayToLong() {
        final long expected = 0x1122_1133_11ee_11ffL;
        final byte[] first = {0x11, 0x22, 0x11, 0x33};
        final byte[] second = {0x11, (byte) 0xee, 0x11, (byte) 0xff};

        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        log.info("current byteBuffer: position: {}/ limit: {}", byteBuffer.position(), byteBuffer.limit());
        byteBuffer.put(first);
        log.info("current byteBuffer: position: {}/ limit: {}", byteBuffer.position(), byteBuffer.limit());
        byteBuffer.put(second);
        log.info("current byteBuffer: position: {}/ limit: {}", byteBuffer.position(), byteBuffer.limit());

        byteBuffer.flip();
        log.info("current byteBuffer: position: {}/ limit: {}", byteBuffer.position(), byteBuffer.limit());
        final long actual = byteBuffer.getLong();
        log.info("current byteBuffer: position: {}/ limit: {}", byteBuffer.position(), byteBuffer.limit());
        log.info("expected: {}, actual: {}", expected, actual);
    }
}
