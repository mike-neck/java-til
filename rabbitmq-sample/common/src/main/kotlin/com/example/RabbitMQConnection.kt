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
package com.example

import com.rabbitmq.client.*
import java.io.Closeable

typealias ChannelAction = (Channel) -> Unit
typealias DeliveryHandler = (String?, Envelope?, AMQP.BasicProperties?, body: ByteArray?) -> Unit

object RabbitMQConnection {
    val sample1: ConnectToMQ = object : ConnectToMQ {
        override val queueName: String
            get() = "sample1"

        override fun connect(action: ChannelAction) =
                connectionFactory().newConnection().use {connection ->  
                    connection.createChannel().asCloseable.use { channel ->
                        channel.queueDelete(queueName)
                        channel.queueDeclare(queueName, false, false, false, emptyMap())
                        action(channel)
                    }
                }

        override fun receive(action: DeliveryHandler) {
            connectionFactory().newConnection().use {connection: Connection ->  
                connection.createChannel().asCloseable.use { channel ->
                    channel.queueDeclare(queueName, false, false, false, emptyMap())
                    println("waiting for message")
                    channel.basicConsume(queueName, consumer(channel, action))
                }
            }
        }

        private fun connectionFactory(): ConnectionFactory {
            val factory = ConnectionFactory()
            factory.host = "localhost"
            return factory
        }
    }

    fun consumer(channel: Channel, handler: DeliveryHandler): Consumer = CustomConsumer(channel, handler)
}

interface QueueName {
    val queueName: String
}

interface ConnectToMQ: QueueName {
    fun receive(action: DeliveryHandler): Unit
    fun connect(action: ChannelAction): Unit
}

class RabbitMQChanel(private val channel: Channel): Channel by channel, Closeable

val Channel.asCloseable: RabbitMQChanel get() = RabbitMQChanel(this)

internal class CustomConsumer(channel: Channel, private val deliveryHandler: DeliveryHandler) : DefaultConsumer(channel) {
    override fun handleDelivery(
            consumerTag: String?, envelope: Envelope?, 
            properties: AMQP.BasicProperties?, body: ByteArray?) =
            deliveryHandler.invoke(consumerTag, envelope, properties, body)
}
