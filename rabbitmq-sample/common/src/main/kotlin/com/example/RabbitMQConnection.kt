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

typealias ChannelAction = QueueName.(Channel) -> Unit
typealias DeliveryHandler = Channel.(String?, Envelope?, AMQP.BasicProperties?, body: ByteArray?) -> Unit

object RabbitMQConnection {
    val sample1: ConnectToMQ = object : ConnectToMQ {
        override val queueName: String
            get() = "sample1"

        override fun connect(action: ChannelAction) = RabbitMQConnection.connect(this, action).useQueue()
                .notDurable().run()

        override fun receive(action: DeliveryHandler): BeforeClose =
                RabbitMQConnection.receive(this, action).notDurable()
                        .noQos().withoutAck()
    }

    private fun connectionFactory(): ConnectionFactory {
        val factory = ConnectionFactory()
        factory.host = "localhost"
        return factory
    }

    private fun consumer(channel: Channel, handler: DeliveryHandler): Consumer = CustomConsumer(channel, handler)

    private fun connect(queueName: QueueName, action: ChannelAction): ProducerConnection = object : ProducerConnection {
        override fun useQueue(): ConnectionQueueDurableMode<Runnable> = object: ConnectionQueueDurableMode<Runnable> {
            override fun useRandomQueueOnExchange(exchangeName: ExchangeName): Runnable = throw UnsupportedOperationException()

            override fun withDurableMode(durable: Boolean): Runnable = Runnable {
                connectionFactory().newConnection().use { connection ->
                    connection.createChannel().asCloseable.use { channel ->
                        channel.queueDelete(queueName.queueName)
                        channel.queueDeclare(queueName.queueName, durable, false, false, emptyMap())
                        action(queueName, channel)
                    }
                }
            }
        }

        override fun useExchange(exchangeName: ExchangeName): Runnable  = Runnable { 
            connectionFactory().newConnection().use { connection -> 
                connection.createChannel().asCloseable.use { channel ->
                    channel.exchangeDeclare(exchangeName.exchangeName, exchangeName.type)
                    action(queueName, channel)
                }
            }
        }

    }

    private fun receive(queueName: QueueName, action: DeliveryHandler): ConnectionQueueDurableMode<BasicQos> =
            object : ConnectionQueueDurableMode<BasicQos> {
                override fun useRandomQueueOnExchange(exchangeName: ExchangeName): BasicQos = object : BasicQos {
                    override fun withQos(qos: Int?): AutoAck = object : AutoAck {
                        override fun withAck(autoAck: Boolean?): BeforeClose = object : BeforeClose {
                            override fun runBeforeClose(handler: (Connection, Channel) -> Unit) =
                                    connectionFactory().newConnection().use { connection ->
                                        connection.createChannel().asCloseable.use { channel ->
                                            if (qos != null) {
                                                channel.basicQos(qos)
                                            }
                                            channel.exchangeDeclare(exchangeName.exchangeName, exchangeName.type)
                                            val name = queueName.queueName(channel)
                                            channel.queueBind(name, exchangeName.exchangeName, "")
                                            if (autoAck != null) {
                                                channel.basicConsume(name, autoAck, consumer(channel, action))
                                            } else {
                                                channel.basicConsume(name, consumer(channel, action))
                                            }
                                            handler(connection, channel)
                                        }
                                    }
                        }
                    }
                }

                override fun withDurableMode(durable: Boolean): BasicQos = object : BasicQos {
                    override fun withQos(qos: Int?): AutoAck = object : AutoAck {
                        override fun withAck(autoAck: Boolean?): BeforeClose = object: BeforeClose {
                            override fun runBeforeClose(handler: (Connection, Channel) -> Unit)  =
                                    connectionFactory().newConnection().use { connection -> 
                                        connection.createChannel().asCloseable.use { channel ->
                                            if (qos != null) {
                                                channel.basicQos(qos)
                                            }
                                            channel.queueDeclare(queueName.queueName, durable, false, false, emptyMap())
                                            if (autoAck != null) {
                                                channel.basicConsume(queueName.queueName, autoAck, consumer(channel, action))
                                            } else{
                                                channel.basicConsume(queueName.queueName, consumer(channel, action))
                                            }
                                            handler(connection, channel)
                                        }
                                    }
                        }
                    }
                }
            }

    val sample2: ConnectToMQ = object : ConnectToMQ {
        override fun receive(action: DeliveryHandler): BeforeClose =
                RabbitMQConnection.receive(this, action).durable().basicQos(10).notAutoAck()

        override fun connect(action: ChannelAction) = RabbitMQConnection.connect(this, action)
                .useQueue().durable().run()

        override val queueName: String get() = "sample-2"
    }

    val sample3: ConnectToMQWithExchangeName = object: ConnectToMQWithExchangeName {
        override val exchangeName: String
            get() = "random-names-3"

        override fun receive(action: DeliveryHandler): BeforeClose = RabbitMQConnection.receive(this, action)
                .useRandomQueueOnExchange(this).noQos().autoAck()

        override fun queueName(channel: Channel): String = channel.queueDeclare().queue

        override fun connect(action: ChannelAction) = RabbitMQConnection.connect(this, action)
                .useExchange(this)
                .run()

        override val type: BuiltinExchangeType get() = BuiltinExchangeType.FANOUT

        override val queueName: String get() = "sample-3"
    }
}

interface QueueName {
    val queueName: String
    fun queueName(channel: Channel): String = queueName
}

interface ExchangeName {
    val exchangeName: String
    val type: BuiltinExchangeType
}

interface ConnectToMQ: QueueName {
    fun receive(action: DeliveryHandler): BeforeClose
    fun connect(action: ChannelAction)
}

interface ProducerConnection {
    fun useQueue(): ConnectionQueueDurableMode<Runnable>
    fun useExchange(exchangeName: ExchangeName): Runnable
}

interface ConnectToMQWithExchangeName: ConnectToMQ, ExchangeName

class RabbitMQChanel(private val channel: Channel): Channel by channel, Closeable

val Channel.asCloseable: RabbitMQChanel get() = RabbitMQChanel(this)

interface BeforeClose {
    fun runBeforeClose(handler: (Connection, Channel) -> Unit)
}

internal interface AutoAck {
    fun autoAck(): BeforeClose = withAck(true)
    fun notAutoAck(): BeforeClose = withAck(false)
    fun withoutAck(): BeforeClose = withAck(null)
    fun withAck(autoAck: Boolean?): BeforeClose
}

internal interface BasicQos {
    fun basicQos(qos: Int): AutoAck = withQos(qos)
    fun noQos(): AutoAck = withQos(null)
    fun withQos(qos: Int?): AutoAck
}

internal class CustomConsumer(channel: Channel, private val deliveryHandler: DeliveryHandler) : DefaultConsumer(channel) {
    override fun handleDelivery(
            consumerTag: String?, envelope: Envelope?, 
            properties: AMQP.BasicProperties?, body: ByteArray?) =
            deliveryHandler(channel, consumerTag, envelope, properties, body)
}

interface ConnectionQueueDurableMode<out N> {
    fun durable(): N = withDurableMode(true)
    fun notDurable(): N = withDurableMode(false)
    fun withDurableMode(durable: Boolean): N
    fun useRandomQueueOnExchange(exchangeName: ExchangeName): N
}
