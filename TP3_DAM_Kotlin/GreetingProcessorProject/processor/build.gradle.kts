plugins {
    kotlin("jvm")
    kotlin("kapt")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))

    // AutoService para registar o processor automaticamente
    implementation("com.google.auto.service:auto-service:1.1.1")
    kapt("com.google.auto.service:auto-service:1.1.1")

    // KotlinPoet para gerar código Kotlin
    implementation("com.squareup:kotlinpoet:1.14.2")

    // Módulo annotations
    implementation(project(":annotations"))
}

kapt {
    correctErrorTypes = true
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}