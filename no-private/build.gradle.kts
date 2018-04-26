plugins {
    id("java-library")
    id("org.junit.platform.gradle.plugin") version("1.0.1")
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.eclipse.collections:eclipse-collections:9.0.0")
    implementation("org.projectlombok:lombok:1.16.18")
    api (group = "io.projectreactor", name = "reactor-core", version = "3.1.6.RELEASE")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.1")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-params", version = "5.0.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.1")
    testImplementation(group = "org.mockito", name = "mockito-core", version = "2.12.0")
}

