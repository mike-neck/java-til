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
package com.example.sub

import com.rabbitmq.client.*

val queueName = "sample1"

fun main(args: Array<String>) {
    val factory = ConnectionFactory()
    factory.host = "localhost"
    factory.newConnection().use { connection -> 
        val channel = connection.createChannel()
        channel.queueDeclare(queueName, false, false, false, null)
        println("waiting for message.")
        channel.basicConsume(queueName, Consumer(0, channel))
    }
}

class Consumer(val index: Int, channel: Channel): DefaultConsumer(channel) {
    override fun handleDelivery(consumerTag: String?, envelope: Envelope?, properties: AMQP.BasicProperties?, body: ByteArray?) =
            (if (body == null) "<null>" else String(body, Charsets.UTF_8))
                    .let { println("[$index] message comes {$it}") }
                    .also { Thread.sleep(20L) }
}
