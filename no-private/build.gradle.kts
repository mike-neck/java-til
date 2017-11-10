plugins {
    id("java-library")
    id("org.junit.platform.gradle.plugin") version("1.0.1")
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.eclipse.collections:eclipse-collections:9.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.1")
}

