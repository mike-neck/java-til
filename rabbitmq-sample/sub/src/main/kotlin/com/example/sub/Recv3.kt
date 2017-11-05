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
    RabbitMQConnection.sample3.receive { _, _, _, body -> 
        val message = if (body != null) {
            String(body, Charsets.UTF_8)
        } else {
            "<null>"
        }
        val count = integer.getAndIncrement()
        println("[$count] receive message [$message]")
        message.split(",").forEach {
            println(it)
            Thread.sleep(500L)
        }
        Thread.sleep(1000L)
        latch.countDown()
        println("[$count] done.")
    }.runBeforeClose { _, _ -> 
        latch.await(50L, TimeUnit.SECONDS)
    }
}
