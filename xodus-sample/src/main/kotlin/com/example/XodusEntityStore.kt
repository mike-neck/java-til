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

import jetbrains.exodus.core.crypto.MessageDigestUtil
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStores
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.*

object XodusEntityStore {

    private val user: User = User.of("test", "Test User", "user@example.com", "test0000----")

    @JvmStatic
    fun main(args: Array<String>) {
        val queue: Queue<EntityId> = LinkedList()

        println("persist single entity")
        val persistentEntityStore = PersistentEntityStores.newInstance(Constant["entity"])
        persistentEntityStore.executeInTransaction { txn ->
            val userEntity = txn.newEntity("User")
            println("new User entity -> ${userEntity.type} - ${userEntity.id}")

            queue.offer(userEntity.id)

            userEntity.setProperty("login", user.login)
            userEntity.setProperty("fullName", user.fullName)
            userEntity.setProperty("email", user.email)
            userEntity.setProperty("salt", user.salt)
            userEntity.setProperty("password", user.password)

            txn.saveEntity(userEntity)
            txn.flush()
        }

        println("get all single entity")
        persistentEntityStore.executeInReadonlyTransaction { txn ->
            val entityIterable = txn.getAll("User")
            entityIterable.forEach { 
                println(User.from(it))
                queue.offer(it.id)
            }
        }

        println("querying all single entity")
        persistentEntityStore.executeInReadonlyTransaction { txn ->
            while (queue.isNotEmpty()) {
                val entityId = queue.poll()
                val entity = txn.getEntity(entityId)
                println(entity.type)
                println(User.from(entity))
            }
        }
    }
}

private data class User(val login: String, val fullName: String, val email: String, val salt: String, val password: String) {

    companion object {
        fun of(login: String, fullName: String, email: String, password: String): User =
                MessageDigestUtil.sha256(SecureRandom.getInstanceStrong().nextLong().toString())
                        .let { User(login, fullName, email, it, MessageDigestUtil.sha256("$it$password")) }

        fun from(entity: Entity): Pair<EntityId, User> =
                entity.id to User(
                        entity.getProperty("login") as String,
                        entity.getProperty("fullName") as String,
                        entity.getProperty("email") as String,
                        entity.getProperty("salt") as String,
                        entity.getProperty("password") as String
                )
    }
}

interface PersistToEntity {
    fun to(entity: Entity)
}

private data class Organization(val name: String, val createdAt: LocalDateTime) {
    companion object {
        fun from(entity: Entity): Pair<EntityId, Organization> =
                entity.id to Organization(
                        entity.getProperty("name") as String,
                        entity.getProperty("createdAt") as LocalDateTime
                )

        fun persist(organization: Organization): PersistToEntity = object: PersistToEntity {
            override fun to(entity: Entity) =
                    entity.setProperty("name", organization.name) and
                            entity.setProperty("createdAt", organization.createdAt).unit
        }
    }
}

private data class Employee(val name: String) {
    companion object {
        fun from(entity: Entity): Pair<EntityId, Employee> = entity.id to
                Employee(entity.getProperty("name") as String)

        
    }
}

private infix fun <A: Any, B: Any> A.and(b: B): B = b
private val <A: Any> A.unit: Unit get() = Unit
