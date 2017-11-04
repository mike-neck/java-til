plugins {
    id("java-library")
    id("org.junit.platform.gradle.plugin")
    kotlin("jvm") version("1.1.51")
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":common"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.1")
}
