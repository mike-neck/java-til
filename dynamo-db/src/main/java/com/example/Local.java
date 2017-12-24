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

import com.amazonaws.services.dynamodbv2.local.main.CommandLineInput;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.local.shared.access.LocalDBAccess;
import com.amazonaws.services.dynamodbv2.local.shared.access.sqlite.SQLiteDBAccess;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.util.Arrays;

public class Local {

    public static void main(String[] args) throws Exception {
        System.out.println("running...");
        System.out.println(System.getProperty("java.library.path"));
        new Local(args).start().join();
    }

    private final CommandLineInput input;

    public Local(String... args) throws ParseException {
        this(new CommandLineInput(args));
    }

    private Local(final CommandLineInput input) {
        this.input = input;
    }

    public Join start() throws Exception {
        if (!input.init()) {
            throw new IllegalStateException();
        }
        final DynamoDBProxyServer server = createServer();
        server.start();
        System.out.println("started port : " + input.getPort());
        return join(server::join).close(server::stop);
    }

    private DynamoDBProxyServer createServer() {
        if (input.shouldOptimizeDBBeforeStartup()) {
            final File[] files = new File(input.getDbPath()).listFiles((dir, name) -> name.endsWith(".db"));
            if (files == null) {
                throw new IllegalStateException();
            }
            Arrays.stream(files)
                    .peek(f -> System.out.println("checking " + f))
                    .map(SQLiteDBAccess::new)
                    .peek(LocalDBAccess::optimizeDBBeforeStartup)
                    .forEach(LocalDBAccess::close);
        }
        return ServerRunner.createServer(input);
    }

    private interface ExRunnable {
        void run() throws Exception;
    }

    private static JoinBuilder join(final ExRunnable runnable) {
        return onClose -> new Join() {
            @Override
            public void join() throws Exception {
                runnable.run();
            }

            @Override
            public void close() throws Exception {
                onClose.run();
            }
        };
    }

    public interface Join extends AutoCloseable {
        void join() throws Exception;
    }

    private interface JoinBuilder {
        Join close(final ExRunnable runnable);
    }
}
