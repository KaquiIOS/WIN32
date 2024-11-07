plugins {
    kotlin("jvm") version "2.0.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation(kotlin("stdlib"))

    // JNA
    implementation("net.java.dev.jna:jna:5.15.0")

    // JNA Platform
    implementation("net.java.dev.jna:jna-platform:5.15.0")

    // Seleinum
    implementation("org.seleniumhq.selenium:selenium-java:4.26.0")
    // https://mvnrepository.com/artifact/org.htmlunit/htmlunit
    implementation("org.htmlunit:htmlunit:4.5.0")

    // Optional: 로깅 설정 (HtmlUnit의 경우 추가하면 디버깅에 유용함)
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.slf4j:slf4j-simple:2.0.9")

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}