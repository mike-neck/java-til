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
package com.example.pub

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory

val queueName = "sample1"

fun main(args: Array<String>) {
    val factory = ConnectionFactory()
    factory.host = "localhost"
    factory.newConnection().use { connection ->
        connection.createChannel().use { channel: Channel ->
            channel.queueDeclare(queueName, false, false, false, emptyMap())

            (1..10).forEach {
                val message = "message - $it".toByteArray(Charsets.UTF_8)
                channel.basicPublish("", queueName, null, message)
                println("pub - sent {$message}")
            }
        }
    }
}

inline fun <C: AutoCloseable, R> C.use(f: (C) -> R): R = try {
    f(this)
} catch (e: Exception) {
    throw e
} finally {
    try {
        this.close()
    } catch (e: Exception) {
    }
}
