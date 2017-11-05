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

import com.example.RabbitMQConnection
import org.kohsuke.randname.RandomNameGenerator
import java.util.*

fun main(args: Array<String>) {
    val random = Random()
    val generator = RandomNameGenerator()
    RabbitMQConnection.sample3.connect { channel -> 
        val times = random.nextInt(15) + 1
        repeat(times) {
            val size = random.nextInt(6)
            val message = (0..size).joinToString(",") { generator.next() }
            println("[$it] published $message")
            channel.basicPublish(RabbitMQConnection.sample3.exchangeName, "", null, message.toByteArray(Charsets.UTF_8))
            Thread.sleep(500L)
        }
    }
}
