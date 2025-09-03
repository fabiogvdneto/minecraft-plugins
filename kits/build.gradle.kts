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
    relocate("com.github.fabiogvdneto.common", "com.github.fabiogvdneto.kits.common")
}
