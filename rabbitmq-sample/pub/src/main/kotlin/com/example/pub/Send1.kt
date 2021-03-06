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

val queueName = "sample1"

fun main(args: Array<String>) {
    RabbitMQConnection.sample1.connect {channel ->  
        (1..10).forEach {
            val message = "message - $it".toByteArray(Charsets.UTF_8)
            channel.basicPublish("", this.queueName, null, message)
            println("pub - sent {$message}")
            Thread.sleep(50L)
        }
    }
}
