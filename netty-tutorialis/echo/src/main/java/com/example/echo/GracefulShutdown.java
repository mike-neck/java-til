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
package com.example.echo;

import lombok.RequiredArgsConstructor;

public interface GracefulShutdown extends AutoCloseable {

    @Override
    default void close() {
        gracefulShutdown();
    }

    void gracefulShutdown();

    static GracefulShutdown gracefulShutdown(final AutoCloseable... resources) {
        final int length = resources.length;
        GracefulShutdown shutdown = NoOpShutdown.instance;
        for (int i = length - 1; i >= 0; i--) {
            shutdown = new Shutdown(resources[i], shutdown);
        }
        return shutdown;
    }
}

final class NoOpShutdown implements GracefulShutdown {

    static NoOpShutdown instance = new NoOpShutdown();

    private NoOpShutdown() {}

    @Override
    public void gracefulShutdown() {
    }
}

@RequiredArgsConstructor
class Shutdown implements GracefulShutdown {

    private final AutoCloseable closeable;
    private final GracefulShutdown next;

    @Override
    public void gracefulShutdown() {
        try {
            closeable.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            next.gracefulShutdown();
        }
    }
}
