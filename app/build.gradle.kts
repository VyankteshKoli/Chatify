@file:Suppress("DEPRECATION")

// build.gradle.kts (Project-level)
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}


repositories {
    google()
    mavenCentral()
}

android {
    compileSdkVersion(34) // Updated to the latest version
    buildToolsVersion("30.0.3")

    namespace = "com.example.chatify" // Namespace specified

    defaultConfig {
        applicationId = "com.example.chatify"
        minSdk = 23 // Change to your desired minSdk version
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // Change to your desired Java version
        targetCompatibility = JavaVersion.VERSION_11 // Change to your desired Java version
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.10.2")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.karumi:dexter:6.0.2")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.squareup.picasso:picasso:2.5.2")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.felipecsl:gifimageview:2.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage:19.2.2")

    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.5.0")

}
