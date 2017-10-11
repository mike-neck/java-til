import org.gradle.kotlin.dsl.getValue

plugins {
    java
    id("org.junit.platform.gradle.plugin")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        options.encoding = "UTF-8"
    }
}

repositories {
    mavenCentral()
}

val eclipseGroup by project
val eclipseCollectionsVersion by project

val junitJupiter by project
val junitVersion by project
val junitApi by project
val junitEngine by project

dependencies {
    implementation("$eclipseGroup:eclipse-collections-api:$eclipseCollectionsVersion")
    implementation("$eclipseGroup:eclipse-collections:$eclipseCollectionsVersion")

    testImplementation("$junitJupiter:$junitApi:$junitVersion")
    testImplementation("$junitJupiter:$junitEngine:$junitVersion")
}
