plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp) // for Room KSP
}

android {
    namespace = "com.example.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.dagger)
    ksp("com.google.dagger:dagger-compiler:${libs.versions.dagger.get()}")

    // Room + KSP
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // --- Unit testing ---
    testImplementation(libs.junit)            // JUnit 4
    testImplementation(libs.mockk)            // MockK
    testImplementation(libs.coroutines.test)  // kotlinx-coroutines-test
    testImplementation(libs.turbine)          // Turbine (only for Flow tests)
}

// Optional: Export Room schemas (for migration testing)
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
