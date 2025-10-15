import com.android.build.gradle.BaseExtension

extensions.configure<BaseExtension> {
    val buildFeatures = javaClass.getMethod("getBuildFeatures").invoke(this)
    buildFeatures.javaClass.getMethod("setCompose", Boolean::class.java).invoke(buildFeatures, true)
}

dependencies {
    add("implementation", platform(libs.compose.bom))
    add("implementation", libs.compose.ui)
    add("implementation", libs.compose.material3)
    add("implementation", libs.activity.compose)
    add("implementation", libs.navigation.compose)
}
