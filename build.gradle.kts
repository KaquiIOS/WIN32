plugins {
    kotlin("jvm") version "2.0.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // JNA
    implementation("net.java.dev.jna:jna:5.15.0")

    // JNA Platform
    implementation("net.java.dev.jna:jna-platform:5.15.0")

    // Seleinum
    implementation("org.seleniumhq.selenium:selenium-java:4.25.0")

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}