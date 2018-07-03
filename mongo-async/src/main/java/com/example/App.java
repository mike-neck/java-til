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

import com.mongodb.MongoClientSettings;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws InterruptedException {
        final MongoClientSettings settings = MongoClientSettings.builder()
                .addCommandListener(new CommandListener() {
                    @Override
                    public void commandStarted(CommandStartedEvent event) {
                        logger.info("command: db = {}, command = {}", event.getDatabaseName(), event.getCommandName());
                    }

                    @Override
                    public void commandSucceeded(CommandSucceededEvent event) {
                        logger.info("command succeed: request = {}, command = {}", event.getRequestId(), event.getCommandName());
                    }

                    @Override
                    public void commandFailed(CommandFailedEvent event) {
                        logger.info("command failed: request = {}, command = {}", event.getRequestId(), event.getCommandName());
                        logger.error("detail", event.getThrowable());
                    }
                })
                .applicationName("sample-app")
                .applyToConnectionPoolSettings(builder -> builder.maxSize(1).minSize(1))
                .build();

        final MongoClient client = MongoClients.create(settings);
        final MongoDatabase database = client.getDatabase("sample");
        final MongoCollection<Document> collection = database.getCollection("test");

        final Document firstDocument = new Document("id", UUID.randomUUID())
                .append("name", "test user")
                .append("created", LocalDateTime.now());

        logger.info("document to be saved: {}", firstDocument);

        final Mono<Document> firstMono = Mono.create(sink ->
                collection.insertOne(firstDocument, (result, t) -> {
                    if (t == null) {
                        logger.info("inserted: {}", firstDocument);
                        sink.success(firstDocument);
                    } else {
                        logger.error("error", t);
                        sink.error(t);
                    }
                }));

        final Mono<List<Document>> secondMono = create100Users(collection, firstMono);

        final Mono<Long> thirdMono = secondMono.then(Mono.create(sink ->
                collection.countDocuments((count, t) -> {
                    if (t == null) {
                        logger.info("collection has {} items.", count);
                        sink.success(count);
                    } else {
                        logger.error("error", t);
                        sink.error(t);
                    }
                })));


        final Mono<List<Document>> fourthMono = create100Users(collection, thirdMono);
        final Mono<List<Document>> fifthMono = create100Users(collection, fourthMono);
        final Mono<List<Document>> sixthMono = create100Users(collection, fifthMono);

        final Mono<Document> seventhMono = sixthMono.then(Mono.create(sink ->
                collection.find().first((doc, t) -> {
                    if (t == null) {
                        logger.info("found document: {}", doc);
                        sink.success(doc);
                    } else {
                        logger.error("error", t);
                        sink.error(t);
                    }
                })));

        final CountDownLatch latch = new CountDownLatch(1);
        seventhMono.doOnTerminate(() -> {
            latch.countDown();
            client.close();
        })
                .subscribe(doc -> logger.info("first document: {}", doc));
        latch.await();
    }

    private static Mono<List<Document>> create100Users(MongoCollection<Document> collection, Mono<?> mono) {
        return mono.then(Mono.create(sink -> {
                final List<Document> documents = IntStream.range(1, 100)
                        .mapToObj(index -> String.format("user-%d", index))
                        .map(name -> new Document("id", UUID.randomUUID()).append("name", name).append("created", LocalDateTime.now()))
                        .collect(Collectors.toList());
                final int size = documents.size();
                logger.info("documents to be saved: {}..{}", documents.get(0), documents.get(size - 1));
                collection.insertMany(documents, (v, t) -> {
                    if (t == null) {
                        logger.info("inserted: {}..{}", documents.get(0), documents.get(size - 1));
                        sink.success(documents);
                    } else {
                        logger.error("error", t);
                        sink.error(t);
                    }
                });
            }));
    }
}
