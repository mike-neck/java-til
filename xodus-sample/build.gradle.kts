import org.gradle.kotlin.dsl.*
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.util.*

plugins {
    id("java-library")
    kotlin("jvm") version("1.1.60")
    id("org.junit.platform.gradle.plugin")
}

repositories {
    jcenter()
}

dependencies {
    api(kotlin(module ="stdlib-jre8", version = "1.1.60"))

    implementation("org.jetbrains.xodus:xodus-openAPI:1.1.0")
    implementation("org.jetbrains.xodus:xodus-environment:1.1.0")
    implementation("org.jetbrains.xodus:xodus-entity-store:1.1.0")

    testImplementation("org.jetbrains.spek:spek-api:1.1.5") {
        exclude(module = "kotlin-reflect")
    }

    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.1.60")
    testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:1.1.5")
}

tasks {
    "envProperties" {
        val file = project.file("src/main/resources/env.properties")
        outputs.files(file)
        doLast {
            Files.write(file.toPath(), listOf("environment=data/.xodus"))
        }
    }
}
