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

import com.example.RabbitMQConnection
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun main(args: Array<String>) {
    val integer = AtomicInteger(0)
    val latch = CountDownLatch(10)
    RabbitMQConnection.sample2.receive { tag, envelope, _, body ->
        val message = if (body == null) {
            "<null>"
        } else {
            String(body, Charsets.UTF_8)
        }
        val count = integer.getAndIncrement()
        println("[$count] receive [$tag/${java.lang.Long.toHexString(envelope?.deliveryTag?: 0)}] '$message'")
        doTask(message)
        if (envelope != null) {
            try {
                this.basicAck(envelope.deliveryTag, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        println("[$count] done")
        latch.countDown()
    }
    latch.await(35000L, TimeUnit.MILLISECONDS)
}

fun doTask(message: String) =
        message.split(",").forEach {
            Thread.sleep(500L)
            println(it)
        }
