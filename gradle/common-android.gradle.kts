import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.gradle.api.JavaVersion


extensions.configure<BaseExtension> {
    compileSdkVersion(libs.versions.compileSdk.get().toInt())
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        vectorDrawables.useSupportLibrary = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

extensions.configure<KotlinAndroidProjectExtension> {
    jvmToolchain(17)
}

dependencies {
    add("implementation", libs.lifecycle.runtime.ktx)
    add("implementation", libs.lifecycle.viewmodel.ktx)
    add("implementation", libs.coroutines.core)
    add("implementation", libs.coroutines.android)
}
