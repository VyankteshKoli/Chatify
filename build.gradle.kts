@file:Suppress("DEPRECATION")

// build.gradle.kts (Project-level)

plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    repositories {
        google() // Ensure this is correctly placed
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.4.1") // Ensure this matches your project's Gradle version
        classpath("com.google.gms:google-services:4.3.15") // Ensure this is the latest compatible version
    }
}

allprojects {
    repositories {
        google() // Ensure this is correctly placed
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

