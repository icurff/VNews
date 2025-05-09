plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
    id("com.google.dagger.hilt.android")

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.vnews"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.vnews"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("com.composables:icons-lucide:1.0.0")

    implementation("com.github.bumptech.glide:glide:5.0.0-rc01")

    implementation("androidx.media3:media3-exoplayer:1.6.1")
    implementation("androidx.media3:media3-ui:1.6.1")
    implementation("androidx.media3:media3-common:1.6.1")
    implementation("androidx.media3:media3-session:1.6.1")

    implementation("androidx.work:work-runtime-ktx:2.10.1")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.prof18.rssparser:rssparser-android:6.0.10")

    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    implementation("androidx.browser:browser:1.9.0-alpha01")

    implementation("androidx.recyclerview:recyclerview:1.4.0")


    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    implementation("org.jsoup:jsoup:1.19.1")
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("androidx.room:room-runtime:2.7.0-rc02")
    implementation(libs.googleid)
    implementation(libs.litert.support.api)
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("com.google.dagger:hilt-android:2.56")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    ksp("com.google.dagger:hilt-compiler:2.56")

    implementation("androidx.navigation:navigation-compose:2.9.0-alpha08")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("androidx.datastore:datastore-preferences:1.1.4")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}