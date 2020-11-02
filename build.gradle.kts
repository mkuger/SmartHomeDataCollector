import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}
group = "me.mikuger"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClassName = "MainKt"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")
    implementation("com.github.kittinunf.fuel:fuel:2.3.0")
    implementation("com.github.kittinunf.fuel:fuel-jackson:2.3.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")
    implementation("com.influxdb:influxdb-client-java:1.12.0")
    implementation("com.sksamuel.hoplite:hoplite-core:1.3.7")
    implementation("com.sksamuel.hoplite:hoplite-yaml:1.3.7")
    implementation("io.github.microutils:kotlin-logging:1.5.9")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}
