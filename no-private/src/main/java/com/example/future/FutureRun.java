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
package com.example.future;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FutureRun {

    private static final ExecutorService ex1 = Executors.newFixedThreadPool(1);
    private static final ExecutorService ex2 = Executors.newFixedThreadPool(2);

    private static void sleep() throws RuntimeException {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static final long SEED = 2L;

    public static void main(String[] args) {
        final CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            sleep();
            return "foo";
        }, ex1);

        System.out.println(new Random(SEED).nextInt(3));

        final CompletableFuture<String> f2 = f1.thenComposeAsync(str -> {
            sleep();
            final Random random = new Random(SEED);
            if (random.nextInt(3) % 2 == 1) {
                System.out.println("fail");
                throw new RuntimeException("error: " + str);
            } else {
                System.out.println("success");
                return CompletableFuture.supplyAsync(() -> {
                    sleep();
                    return String.format("%s - %s", str, str);
                }, ex2);
            }
        }, ex2);

        final CompletableFuture<String> future = new CompletableFuture<>();

        f2.whenComplete((string, th) -> {
            System.out.println(String.format("result: [%s]/[%s]", string, th));
            if (th == null) {
                System.out.println("success - final");
                final String result = String.format("result: %s", string);
                future.complete(result);
            } else {
                System.out.println("failure - final");
                future.completeExceptionally(th);
            }
        });

        try {
            final String result = future.get();
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e);
            System.out.println(e.getMessage());
            final Throwable th = e.getCause();
            if (th != null) {
                System.out.println(th.getClass());
                System.out.println(th.getMessage());
            }
        }
        ex1.shutdown();
        ex2.shutdown();
    }
}
