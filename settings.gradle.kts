// settings.gradle.kts
pluginManagement {
    repositories {
        google() // Repository for Google's plugins
        mavenCentral() // Repository for Maven Central
        gradlePluginPortal() // Repository for the Gradle Plugin Portal
    }
}

include(":app")

dependencyResolutionManagement {
    repositories {
        google() // Repository for Google's dependencies
        mavenCentral() // Repository for Maven Central
    }

}
