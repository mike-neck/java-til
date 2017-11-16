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

import jetbrains.exodus.bindings.StringBinding
import jetbrains.exodus.env.Cursor
import jetbrains.exodus.env.Environments
import jetbrains.exodus.env.StoreConfig
import java.util.*

fun main(args: Array<String>) {
    Environments.newInstance(Constant["environment"]).use {env -> 
        env.executeInTransaction { txn -> 
            val key = StringBinding.stringToEntry("hello")
            val value = StringBinding.stringToEntry("World.")
            val store = env.openStore("Messages", StoreConfig.WITHOUT_DUPLICATES, txn)
            store.put(txn, key, value)
            store.put(txn, StringBinding.stringToEntry("foo"), StringBinding.stringToEntry("bar"))
            txn.flush()
        }

        env.executeInReadonlyTransaction { txn ->
            print("Message store exists? -> ")
            println(env.storeExists("Messages", txn))

            print("Not existing store -> ")
            println(env.storeExists("foo", txn))

            println("all stores")
            env.getAllStoreNames(txn).forEach { 
                println(it)
            }
        }

        env.executeInReadonlyTransaction { txn ->
            val store = env.openStore("Messages", StoreConfig.USE_EXISTING, txn)
            store.openCursor(txn).use { cur ->
                tailrec fun tailRecFun(c: Cursor): Unit = when(c.next) {
                    false -> Unit
                    true -> {
                        val key = StringBinding.entryToString(cur.key)
                        val value = StringBinding.entryToString(cur.value)
                        println("$key -> $value")
                        tailRecFun(c)
                    }
                }
                tailRecFun(cur)
            }
        }
    }
}

object Constant {
    val classLoader: ClassLoader get() = Constant::class.java.classLoader

    val properties: Properties by lazy { 
        (classLoader.getResourceAsStream("env.properties") ?: throw IllegalStateException("not found env.properties"))
                .use { it.reader(Charsets.UTF_8).let { r -> Properties().apply { load(r) } } }
    }

    fun getProperty(key: String): String? = properties.getProperty(key)

    operator fun get(key: String): String = "${getProperty(key)}"
}
