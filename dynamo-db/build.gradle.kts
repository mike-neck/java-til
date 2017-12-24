import java.net.URI

plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven {
        url = URI.create("https://s3-ap-northeast-1.amazonaws.com/dynamodb-local-tokyo/release")
    }
}

dependencies {
    compile("com.amazonaws:DynamoDBLocal:1.11.86")
    compile("com.amazonaws:aws-java-sdk-dynamodb:1.11.255")
}

tasks {
    "sqlite4"(Copy::class) {
        from(configurations.getAt("compile").find { it.name == "libsqlite4java-osx-1.0.392.dylib" })
        into("$buildDir/sqlite4")
    }
}
