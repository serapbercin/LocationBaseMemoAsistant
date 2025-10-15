import org.gradle.kotlin.dsl.testImplementation

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ksp) // for Dagger KSP

}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

dependencies {
    api(libs.coroutines.core)

    implementation(libs.dagger)
    ksp("com.google.dagger:dagger-compiler:${libs.versions.dagger.get()}")


    // --- Unit testing ---
    testImplementation(libs.junit)            // JUnit 4
    testImplementation(libs.mockk)            // MockK
    testImplementation(libs.coroutines.test)  // kotlinx-coroutines-test
    testImplementation(libs.turbine)          // Turbine (only for Flow tests)
}
