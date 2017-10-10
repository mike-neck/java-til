plugins {
    java
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // buildscript スコープの中では val propName by project が解決できない(おそらくkotlin dslが読み込まれていない)
        // これに近い
        // https://github.com/gradle/kotlin-dsl/issues/472
        classpath("${property("junitGradlePlugin")}:${property("junitGradleVersion")}")
        // というか、issue建てた
        // https://github.com/gradle/kotlin-dsl/issues/535
    }
}

val junitPlugin by project

apply {
    plugin("$junitPlugin")
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
