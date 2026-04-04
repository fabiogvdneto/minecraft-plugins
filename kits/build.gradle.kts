plugins {
    id("java")
    alias(libs.plugins.shadow)
}

group = "com.github.fabiogvdneto"
version = "0.1"

dependencies {
    implementation(project(":common"))
    compileOnly(libs.papermc)
}

tasks.shadowJar {
    // Avoid package conflicts between plugins.
    relocate("com.github.fabiogvdneto.common", "com.github.fabiogvdneto.kits.common")

    // Remove "-all" suffix from the output file.
    archiveClassifier.set("")

    // Change the output directory by using this property:
    // -Pout=/your/custom/path
    project.findProperty("out")?.let { destinationDirectory.set(file(it)) }
}
