package com.example

import java.util.*

object Constant {
    val classLoader: ClassLoader get() = Constant::class.java.classLoader

    val properties: Properties by lazy { 
        (classLoader.getResourceAsStream("env.properties") ?: throw IllegalStateException("not found env.properties"))
                .use { it.reader(Charsets.UTF_8).let { r -> Properties().apply { load(r) } } }
    }

    fun getProperty(key: String): String? = properties.getProperty(key)

    operator fun get(key: String): String = "${getProperty(key)}"
}
