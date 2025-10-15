dependencies {
    add("implementation", libs.room.runtime)
    add("implementation", libs.room.ktx)
    add("ksp", libs.room.compiler)
}

ksp {
    arg("room.schemaLocation", "${project.projectDir}/schemas")
}
