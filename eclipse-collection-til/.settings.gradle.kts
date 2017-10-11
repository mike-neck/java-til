pluginManagement {
    repositories {
        maven {
            url("http://repo1.maven.org/maven2/")
        }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "$junitPlugin") {
                useModule("$junitGradlePlugin:$junitGradleVersion")
            }
        }
    }
}

include("case1")
rootProject.name = "eclipse-collection-til"
